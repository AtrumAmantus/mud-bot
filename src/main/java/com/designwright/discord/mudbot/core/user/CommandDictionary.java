package com.designwright.discord.mudbot.core.user;

import lombok.Data;

import java.util.Map;

@Data
public class CommandDictionary {

    private final Map<String, Command> commandMap;

    public boolean hasCommand(String commandName) {
        return commandMap.containsKey(commandName);
    }

    public Command getCommand(String commandName) {
        Command command;

        if (commandMap.containsKey(commandName)) {
            command = commandMap.get(commandName);
        } else {
            command = new Command();
        }

        return command;
    }

}
