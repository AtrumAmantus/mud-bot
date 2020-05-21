package com.designwright.discord.discordscape.core.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class InternalApi {

    private final InternalGateway internalBroker;

    public <T> InternalRequest<T> sendAndReceive(InternalRequest<T> internalEvent) {
        return internalBroker.sendAndReceive(internalEvent);
    }

    public <T> void send(InternalRequest<T> internalRequest) {
        internalBroker.send(internalRequest);
    }
}