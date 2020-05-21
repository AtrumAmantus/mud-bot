package com.designwright.discord.discordscape.core.exception;

public class InternalRequestException extends RuntimeException {
    public InternalRequestException(String message) {
        super(message);
    }

    public InternalRequestException(String message, Object object) {
        super(message + ": " + object.toString());
    }

    public InternalRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
