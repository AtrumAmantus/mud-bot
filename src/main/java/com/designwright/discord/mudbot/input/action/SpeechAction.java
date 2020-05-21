package com.designwright.discord.mudbot.input.action;

import lombok.Data;

@Data
public abstract class SpeechAction implements UserAction {

    private final String message;
    private String prefix;

    public String getMessage() {
        return prefix + message;
    }

}
