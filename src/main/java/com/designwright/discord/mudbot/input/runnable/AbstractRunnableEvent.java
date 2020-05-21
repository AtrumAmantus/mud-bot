package com.designwright.discord.mudbot.input.runnable;

import com.designwright.discord.mudbot.core.request.InternalApi;
import com.designwright.discord.mudbot.input.message.MessageEvent;
import com.designwright.discord.mudbot.input.message.MessageParserFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractRunnableEvent implements Runnable {

    protected final InternalApi internalApi;
    protected final MessageParserFactory messageParserFactory;
    protected final MessageEvent messageEvent;

}
