package com.designwright.discord.mudbot.configurations;

import com.designwright.discord.mudbot.core.user.Command;
import com.designwright.discord.mudbot.core.user.CommandDictionary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ApplicationConfiguration {

    ExecutorService executorService;

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }

    @Bean
    public ExecutorService executorService() {
        executorService = Executors.newFixedThreadPool(10);
        return executorService;
    }

    @Bean
    public CommandDictionary commandDictionary() {
        Command loginCommand = new Command();
        loginCommand.setName("login");
        loginCommand.setArgumentCount(0);
        loginCommand.setDescription("Logs you into the system, initializing interactions");
        loginCommand.setUsage("/login");
        Command logoutCommand = new Command();
        logoutCommand.setName("logout");
        logoutCommand.setArgumentCount(0);
        logoutCommand.setDescription("Logs you out of the system.");
        logoutCommand.setUsage("/logout");

        Map<String, Command> commandMap = new HashMap<>();
        commandMap.put(loginCommand.getName(), loginCommand);
        commandMap.put(logoutCommand.getName(), logoutCommand);
        return new CommandDictionary(commandMap);
    }

}
