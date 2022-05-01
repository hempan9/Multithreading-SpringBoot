package com.hobject.multithreadingspringboot.config;

import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class TaskExecutorConfig {
    @Bean(name = "taskExecutor")
    Executor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4); //thread count
        executor.setMaxPoolSize(4); //max thread count
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("userThread");
        executor.initialize();
        return executor;

    }
}
