package com.designwright.discord.discordscape.input.action;

import lombok.Data;

@Data
public abstract class AvatarMenuAction implements UserAction {

    private final String message;

}
