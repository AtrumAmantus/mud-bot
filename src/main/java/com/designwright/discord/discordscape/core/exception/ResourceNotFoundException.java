package com.designwright.discord.discordscape.core.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super("Resource with the specified ID does not exist.");
    }
}
