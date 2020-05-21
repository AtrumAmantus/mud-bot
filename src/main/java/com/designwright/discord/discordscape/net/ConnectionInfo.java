package com.designwright.discord.discordscape.net;

import com.designwright.discord.discordscape.data.domain.Avatar;
import com.designwright.discord.discordscape.data.domain.User;
import lombok.Data;
import net.dv8tion.jda.api.entities.PrivateChannel;

@Data
public class ConnectionInfo {

    private final net.dv8tion.jda.api.entities.User discordUser;
    private final PrivateChannel privateChannel;
    private User user;
    private Avatar avatar;

}
