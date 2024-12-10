package com.twitterclone.configs;

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
class SchedulerConfig {

    @Bean
    ThreadPoolTaskScheduler taskScheduler() {
        final def scheduler = new ThreadPoolTaskScheduler()
        scheduler.setPoolSize(10)
        scheduler.setThreadNamePrefix("ScheduledTask-")
        return scheduler
    }
}