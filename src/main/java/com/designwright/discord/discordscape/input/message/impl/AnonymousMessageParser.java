package com.designwright.discord.discordscape.input.message.impl;

import com.designwright.discord.discordscape.input.action.ConnectionAction;
import com.designwright.discord.discordscape.input.action.InvalidAction;
import com.designwright.discord.discordscape.input.enums.UserType;
import com.designwright.discord.discordscape.input.message.MessageEvent;
import com.designwright.discord.discordscape.input.action.Actionable;
import com.designwright.discord.discordscape.input.message.MessageParser;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class AnonymousMessageParser extends MessageParser {

    public AnonymousMessageParser(MessageEvent messageEvent) {
        super(messageEvent, UserType.ANONYMOUS);
    }

    public Actionable parse() {
        PrivateMessageReceivedEvent event = messageEvent.getEvent();
        Actionable actionable = new Actionable();
        actionable.setSource(event.getAuthor());
        actionable.setOrigin(event.getChannel());
        String message = event.getMessage().getContentRaw();

        if (message.equalsIgnoreCase("login")) {
            actionable.setTarget(event.getAuthor().getAvatarId());
            actionable.setAction(new ConnectionAction(ConnectionAction.Action.LOGIN));
        } else {
            actionable.setAction(new InvalidAction("You must login to begin. Type 'login'."));
        }

        return actionable;
    }

}
