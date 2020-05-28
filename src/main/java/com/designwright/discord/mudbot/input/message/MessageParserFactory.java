package com.designwright.discord.mudbot.input.message;

import com.designwright.discord.mudbot.core.user.CommandDictionary;
import com.designwright.discord.mudbot.input.message.impl.SimpleMessageParser;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MessageParserFactory {

    private final CommandDictionary commandDictionary;
    private final List<MessageParser> messageParserList;
    private Map<String, MessageParser> messageParserMap;

    @PostConstruct
    public void init() {
        messageParserMap = new HashMap<>();
        messageParserList.forEach(messageParser -> messageParserMap.put(messageParser.getClass().getSimpleName(), messageParser));
    }

    public MessageParser createParser(PrivateMessageReceivedEvent event) {
        MessageParser messageParser;

        try {
            messageParser = messageParserMap.get(SimpleMessageParser.class.getSimpleName())
                    .getClass()
                    .getConstructor(MessageEvent.class, CommandDictionary.class)
                    .newInstance(new MessageEvent(event), commandDictionary);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("Could not execute RunnableEvent", e);
        }

        return messageParser;
    }

}
