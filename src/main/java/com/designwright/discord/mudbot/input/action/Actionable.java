package com.designwright.discord.mudbot.input.action;

import lombok.Data;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

@Data
public class Actionable {

    private User source;
    private String target;
    private PrivateChannel origin;
    private UserAction action;

}
