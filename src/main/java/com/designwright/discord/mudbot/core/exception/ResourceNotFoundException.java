package com.designwright.discord.mudbot.core.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super("Resource with the specified ID does not exist.");
    }
}
