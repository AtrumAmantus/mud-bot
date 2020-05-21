package com.designwright.discord.discordscape.input.message.impl;

import com.designwright.discord.discordscape.input.action.avatarmenu.CreateAvatarAction;
import com.designwright.discord.discordscape.input.action.avatarmenu.SelectAvatarAction;
import com.designwright.discord.discordscape.input.enums.UserType;
import com.designwright.discord.discordscape.input.message.MessageEvent;
import com.designwright.discord.discordscape.input.action.Actionable;
import com.designwright.discord.discordscape.input.action.InvalidAction;
import com.designwright.discord.discordscape.input.message.MessageParser;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class UnknownMessageParser extends MessageParser {

    public UnknownMessageParser(MessageEvent messageEvent) {
        super(messageEvent, UserType.UNKNOWN);
    }

    public Actionable parse() {
        PrivateMessageReceivedEvent event = messageEvent.getEvent();
        Actionable actionable = new Actionable();
        actionable.setSource(event.getAuthor());
        actionable.setOrigin(event.getChannel());
        String message = event.getMessage().getContentRaw();

        if (message.startsWith("create ")) {
            actionable.setTarget(event.getAuthor().getAvatarId());
            actionable.setAction(
                    new CreateAvatarAction(
                            formatMessage(message, "create ")
                    )
            );
        } else if (message.startsWith("use ")) {
            actionable.setTarget(event.getAuthor().getAvatarId());
            actionable.setAction(
                    new SelectAvatarAction(
                            formatMessage(message, "use ")
                    )
            );
        } else {
            actionable.setAction(new InvalidAction("Invalid action, you must either create or select an avatar."));
        }

        return actionable;
    }

}
