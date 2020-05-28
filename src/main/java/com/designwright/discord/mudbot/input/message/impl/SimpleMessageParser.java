package com.designwright.discord.mudbot.input.message.impl;

import com.designwright.discord.mudbot.core.user.Command;
import com.designwright.discord.mudbot.core.user.CommandDictionary;
import com.designwright.discord.mudbot.input.action.Actionable;
import com.designwright.discord.mudbot.input.action.AnimateAction;
import com.designwright.discord.mudbot.input.action.CommandAction;
import com.designwright.discord.mudbot.input.action.InvalidAction;
import com.designwright.discord.mudbot.input.action.UserAction;
import com.designwright.discord.mudbot.input.message.MessageEvent;
import com.designwright.discord.mudbot.input.message.MessageParser;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SimpleMessageParser extends MessageParser {

    private static final String COMMAND = "command";
    private static final String ACTION = "action";
    private static final Pattern commandPattern = Pattern.compile("^/(?<" + COMMAND + ">[a-zA-Z]+)(\\s(?<" + ACTION + ">.*))?");

    public SimpleMessageParser(MessageEvent messageEvent, CommandDictionary commandDictionary) {
        super(messageEvent, commandDictionary);
    }

    public Actionable parse() {
        PrivateMessageReceivedEvent event = messageEvent.getEvent();
        Actionable actionable = new Actionable();
        actionable.setSource(event.getAuthor());
        actionable.setOrigin(event.getChannel());
        String message = event.getMessage().getContentRaw();
        UserAction userAction;

        if (StringUtils.startsWith(message, "/")) {
            userAction = createCommandAction(message);
        } else {
            userAction = createActionAction(message);
        }

        actionable.setAction(userAction);

        return actionable;
    }

    UserAction createCommandAction(String message) {
        UserAction userAction;
        Matcher matcher = commandPattern.matcher(message);

        if (matcher.matches()) {
            String commandName = matcher.group(COMMAND);
            if (commandDictionary.hasCommand(commandName)) {
                Command command = commandDictionary.getCommand(commandName);
                String action = matcher.group(ACTION);
                List<String> arguments;

                if (action != null) {
                    arguments = Arrays.asList(
                            matcher.group(ACTION).split(" ", command.getArgumentCount())
                    );
                } else {
                    arguments = new ArrayList<>();
                }

                if (arguments.size() == command.getArgumentCount()) {
                    userAction = new CommandAction(commandName, arguments);
                } else {
                    userAction = new InvalidAction("Invalid use, correct usage: " + command.getUsage());
                }
            } else {
                userAction = new InvalidAction("'/" + commandName + "' is not a valid command. Are you hacking?");
            }
        } else {
            userAction = new InvalidAction("'/' must be immediately followed by a valid command.");
        }

        return userAction;
    }

    UserAction createActionAction(String message) {
        //TODO: Create parser for action grammar
        return new AnimateAction(message);
    }

}
