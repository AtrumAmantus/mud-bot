package com.designwright.discord.mudbot.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "discord")
public class DiscordConfiguration {

    private String apiToken;

}
