package com.designwright.discord.mudbot.net;

import com.designwright.discord.mudbot.input.runnable.AbstractRunnableEvent;
import com.designwright.discord.mudbot.input.runnable.RunnableEventFactory;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
public class BotListenerAdapter extends ListenerAdapter {

    private final ExecutorService executorService;
    private final RunnableEventFactory runnableEventFactory;

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        AbstractRunnableEvent runnableEvent = runnableEventFactory.createRunnableEvent(event);
        executorService.submit(runnableEvent);
    }

}
