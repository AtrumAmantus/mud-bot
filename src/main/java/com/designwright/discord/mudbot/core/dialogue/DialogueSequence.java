package com.designwright.discord.mudbot.core.dialogue;

import com.designwright.discord.mudbot.core.request.InternalApi;
import com.designwright.discord.mudbot.net.ConnectionInfo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class DialogueSequence {

    protected final InternalApi internalApi;

    protected final ConnectionInfo userConnectionInfo;

    public abstract void next();

    public abstract String query(String message);

    protected void replyToUser(String message) {
        userConnectionInfo.getPrivateChannel().sendMessage(message).queue();
    }

}
