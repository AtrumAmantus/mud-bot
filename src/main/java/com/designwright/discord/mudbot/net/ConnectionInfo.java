package com.designwright.discord.mudbot.net;

import com.designwright.discord.mudbot.data.domain.Avatar;
import com.designwright.discord.mudbot.data.domain.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.PrivateChannel;

@Data
@RequiredArgsConstructor
public class ConnectionInfo {

    private final net.dv8tion.jda.api.entities.User discordUser;
    private final PrivateChannel privateChannel;
    private final User user;
    private Avatar avatar;

    public ConnectionInfo(ConnectionInfo other) {
        this.discordUser = other.discordUser;
        this.privateChannel = other.privateChannel;
        this.user = other.user;
        this.avatar = other.avatar;
    }
}
