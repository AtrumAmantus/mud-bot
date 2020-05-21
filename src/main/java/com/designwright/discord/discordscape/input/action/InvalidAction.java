package com.designwright.discord.discordscape.input.action;

import lombok.Data;

@Data
public class InvalidAction implements UserAction {

    private final String message;

}
