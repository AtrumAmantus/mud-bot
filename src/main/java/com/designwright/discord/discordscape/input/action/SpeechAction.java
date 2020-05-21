package com.designwright.discord.discordscape.input.action;

import lombok.Data;

@Data
public abstract class SpeechAction implements UserAction {

    private final String message;
    private String prefix;

    public String getMessage() {
        return prefix + message;
    }

}
