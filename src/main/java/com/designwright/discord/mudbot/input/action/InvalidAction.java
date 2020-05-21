package com.designwright.discord.mudbot.input.action;

import lombok.Data;

@Data
public class InvalidAction implements UserAction {

    private final String message;

}
