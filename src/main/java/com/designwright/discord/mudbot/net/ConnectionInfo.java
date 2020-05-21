package com.designwright.discord.mudbot.net;

import com.designwright.discord.mudbot.data.domain.Avatar;
import com.designwright.discord.mudbot.data.domain.User;
import lombok.Data;
import net.dv8tion.jda.api.entities.PrivateChannel;

@Data
public class ConnectionInfo {

    private final net.dv8tion.jda.api.entities.User discordUser;
    private final PrivateChannel privateChannel;
    private User user;
    private Avatar avatar;

}
