package com.designwright.discord.mudbot.core.request;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class InternalRequest<T> {

    private final List<T> payload;
    private final Type type;
    private final Status status;
    private final String message;

    public enum Type {
        CREATE,
        READ,
        UPDATE,
        DELETE
    }

    public enum Status {
        OK,
        WARNING,
        ERROR
    }

    public T getPayloadFirst() {
        return payload.get(0);
    }

    public InternalRequest(T payload, Type type) {
        this(Collections.singletonList(payload), type);
    }

    public InternalRequest(List<T> payload, Type type) {
        this(payload, type, Status.OK, null);
    }

    public InternalRequest(List<T> payload, Type type, Status status, String message) {
        this.payload = payload;
        this.type = type;
        this.status = status;
        this.message = message;
    }

}
