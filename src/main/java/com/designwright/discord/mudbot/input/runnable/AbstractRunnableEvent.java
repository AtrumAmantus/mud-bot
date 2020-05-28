package com.designwright.discord.mudbot.input.runnable;

import com.designwright.discord.mudbot.core.request.InternalApi;
import com.designwright.discord.mudbot.input.message.MessageEvent;
import com.designwright.discord.mudbot.input.message.MessageParserFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractRunnableEvent implements Runnable {

    protected final InternalApi internalApi;
    protected final MessageParserFactory messageParserFactory;
    protected final MessageEvent messageEvent;

    @Override
    public void run() {
        try {
            runEvent();
        } catch (Exception e) {
            log.error("Uncaught exception: ", e);
        }
    }

    protected abstract void runEvent();
}
