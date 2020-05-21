package com.designwright.discord.discordscape.input.runnable;

import com.designwright.discord.discordscape.core.request.InternalApi;
import com.designwright.discord.discordscape.input.message.MessageEvent;
import com.designwright.discord.discordscape.input.message.MessageParserFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractRunnableEvent implements Runnable {

    protected final InternalApi internalApi;
    protected final MessageParserFactory messageParserFactory;
    protected final MessageEvent messageEvent;

}
