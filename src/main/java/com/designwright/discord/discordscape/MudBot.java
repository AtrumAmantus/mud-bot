package com.designwright.discord.discordscape;

import com.designwright.discord.discordscape.configurations.DiscordConfiguration;
import com.designwright.discord.discordscape.net.BotListenerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MudBot {

    private final BotListenerAdapter botListenerAdapter;
    private final DiscordConfiguration discordConfiguration;

    @PostConstruct
    public void init() {
        JDABuilder builder = JDABuilder
                .createDefault(discordConfiguration.getApiToken())
                .addEventListeners(botListenerAdapter);
        try {
            builder.build();
        } catch (LoginException e) {
            log.error(e.getMessage(), e);
        }
    }

}
