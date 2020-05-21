package com.designwright.discord.mudbot.input.action;

import lombok.Data;

@Data
public abstract class AvatarMenuAction implements UserAction {

    private final String message;

}
