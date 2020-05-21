package com.designwright.discord.mudbot.input.message.impl;

import com.designwright.discord.mudbot.input.action.Actionable;
import com.designwright.discord.mudbot.input.action.AnimateAction;
import com.designwright.discord.mudbot.input.action.ConnectionAction;
import com.designwright.discord.mudbot.input.action.speech.AvatarSpeechAction;
import com.designwright.discord.mudbot.input.action.speech.UserSpeechAction;
import com.designwright.discord.mudbot.input.enums.UserType;
import com.designwright.discord.mudbot.input.message.MessageEvent;
import com.designwright.discord.mudbot.input.message.MessageParser;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class AvatarMessageParser extends MessageParser {

    private static final String tellTargetGroup = "Target";
    private static final String tellPrefixRegex = "^/tell (?<" + tellTargetGroup + ">\\S+)";
    private static final Pattern tellPrefixPattern = Pattern.compile(tellPrefixRegex);

    public AvatarMessageParser(MessageEvent messageEvent) {
        super(messageEvent, UserType.AVATAR);
    }

    public Actionable parse() {
        PrivateMessageReceivedEvent event = messageEvent.getEvent();
        Actionable actionable = new Actionable();
        actionable.setSource(event.getAuthor());
        actionable.setOrigin(event.getChannel());
        String message = event.getMessage().getContentRaw();

        if (message.equalsIgnoreCase("logout")) {
            actionable.setTarget(event.getAuthor().getId());
            actionable.setAction(new ConnectionAction(ConnectionAction.Action.LOGOUT));
        } else {
            if (message.startsWith("/")) {
                if (message.startsWith("/say ")) {
                    actionable.setAction(
                            new AvatarSpeechAction(
                                    formatMessage(message, "/say")
                            )
                    );
//                } else if (message.startsWith("/tell")) {
//                    Matcher matcher = tellPrefixPattern.matcher(message);
//                    if (matcher.matches()) {
//                        actionable.setTarget(matcher.group(tellTargetGroup));
//                    }
//                    actionable.setAction(
//                            new AvatarSpeechAction(
//                                    formatMessage(message, tellPrefixRegex)
//                            )
//                    );
                } else if (message.startsWith("/ooc ")) {
                    actionable.setAction(
                            new UserSpeechAction(
                                    formatMessage(message, "/ooc")
                            )
                    );
                }
            } else {
                actionable.setAction(new AnimateAction(message));
            }
        }

        return actionable;
    }

}
