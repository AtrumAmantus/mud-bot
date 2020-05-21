package com.designwright.discord.mudbot.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ApplicationConfiguration {

    ExecutorService executorService;

    @Bean
    public ExecutorService executorService() {
        executorService = Executors.newFixedThreadPool(10);
        return executorService;
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }

}
