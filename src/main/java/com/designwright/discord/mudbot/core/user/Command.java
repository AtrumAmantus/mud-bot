package com.designwright.discord.mudbot.core.user;

import lombok.Data;

@Data
public class Command {

    private String name;
    private int argumentCount;
    private String description;
    private String usage;

}
