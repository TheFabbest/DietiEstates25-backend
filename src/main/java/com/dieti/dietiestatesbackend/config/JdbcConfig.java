package com.dieti.dietiestatesbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.dieti.dietiestatesbackend.util.DaemonThreadFactory;

@Configuration
@EnableScheduling
public class JdbcConfig {

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(1, new DaemonThreadFactory());
    }
}