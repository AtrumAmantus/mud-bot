package com.designwright.discord.discordscape.input.message;

import lombok.Data;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.springframework.stereotype.Component;

@Data
@Component
public class MessageEvent {

    private final PrivateMessageReceivedEvent event;

    public MessageEvent() {
        event = null;
    }

    public MessageEvent(PrivateMessageReceivedEvent event) {
        this.event = event;
    }

}
