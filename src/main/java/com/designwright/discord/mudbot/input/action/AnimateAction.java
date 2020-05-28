package com.designwright.discord.mudbot.input.action;

import lombok.Data;

@Data
public class AnimateAction implements UserAction {

    private final String phrase;
    private String verb;
    private String preposition;
    private String determiner;
    private String object;

}
