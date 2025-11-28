// src/main/java/com/rahul/genmillenauts/aiservice/config/AsyncConfig.java
package com.rahul.genmillenauts.aiservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/*
  Async executor configuration used by the stress engine for background tasks.
*/
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "stressExecutor")
    public Executor stressExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("stress-exec-");
        executor.initialize();
        return executor;
    }
}
