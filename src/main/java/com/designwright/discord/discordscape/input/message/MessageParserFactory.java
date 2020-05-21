package com.designwright.discord.discordscape.input.message;

import com.designwright.discord.discordscape.input.enums.UserType;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MessageParserFactory {

    private final List<MessageParser> messageParserList;
    private Map<UserType, MessageParser> messageParserMap;

    @PostConstruct
    public void init() {
        messageParserMap = new EnumMap<>(UserType.class);
        messageParserList.forEach(messageParser -> messageParserMap.put(messageParser.getUserType(), messageParser));
    }

    public MessageParser createParser(PrivateMessageReceivedEvent event, UserType userType) {
        MessageParser messageParser;

        try {
            messageParser = messageParserMap.get(userType)
                    .getClass()
                    .getConstructor(MessageEvent.class)
                    .newInstance(new MessageEvent(event));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("Could not execute RunnableEvent", e);
        }

        return messageParser;
    }

}
