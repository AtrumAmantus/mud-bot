package com.designwright.discord.mudbot.input.action;

import lombok.Data;

import java.util.List;

@Data
public class CommandAction implements UserAction {

    private final String commandName;
    private final List<String> arguments;

}
