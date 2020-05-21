package com.designwright.discord.mudbot.input.action;

import lombok.Data;

@Data
public class ConnectionAction implements UserAction {

    public enum Action {
        LOGIN,
        LOGOUT
    }

    private final String message;
    private final Action action;

    public ConnectionAction(Action action) {
        this.action = action;
        if (Action.LOGIN.equals(action)) {
            this.message = "connected.";
        } else {
            this.message = "disconnected.";
        }
    }
}
