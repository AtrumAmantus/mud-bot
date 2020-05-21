package com.designwright.discord.mudbot.input.runnable;

import com.designwright.discord.mudbot.core.request.InternalApi;
import com.designwright.discord.mudbot.input.message.MessageEvent;
import com.designwright.discord.mudbot.input.message.MessageParserFactory;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RunnableEventFactory {

    private final Map<String, AbstractRunnableEvent> runnableEventMap;
    private final InternalApi internalApi;
    private final MessageParserFactory messageParserFactory;

    public RunnableEventFactory(
            List<AbstractRunnableEvent> runnableEvents,
            InternalApi internalApi,
            MessageParserFactory messageParserFactory
    ) {
        this.internalApi = internalApi;
        this.messageParserFactory = messageParserFactory;
        runnableEventMap = new HashMap<>();
        runnableEvents.forEach(runnableEvent -> runnableEventMap.put(PrivateMessageReceivedEvent.class.getSimpleName(), runnableEvent));
    }

    public AbstractRunnableEvent createRunnableEvent(PrivateMessageReceivedEvent event) {
        AbstractRunnableEvent runnableEvent;
        String className = event.getClass().getSimpleName();

        if (runnableEventMap.containsKey(className)) {
            try {
                runnableEvent = runnableEventMap
                        .get(className)
                        .getClass()
                        .getConstructor(
                                InternalApi.class,
                                MessageParserFactory.class,
                                MessageEvent.class
                        )
                        .newInstance(
                                internalApi,
                                messageParserFactory,
                                new MessageEvent(event)
                        );
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new IllegalStateException("Could not execute RunnableEvent", e);
            }
        } else {
            throw new IllegalArgumentException("No RunnableEvent for the given EventType");
        }

        return runnableEvent;
    }

}
