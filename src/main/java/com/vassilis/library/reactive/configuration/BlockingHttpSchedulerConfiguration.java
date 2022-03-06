package com.vassilis.library.reactive.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
public class BlockingHttpSchedulerConfiguration {

    /**
     * Scheduler with threads that can be blocked. This can be used as a work-around to overcome executing paths that
     * are blocked.
     *
     * @param blockingHttpSchedulerProps
     * @return
     */
    @Bean
    public Scheduler blockingHttpScheduler(BlockingHttpSchedulerProperties blockingHttpSchedulerProps) {
        return Schedulers.newParallel(blockingHttpSchedulerProps.getNumOfThreads(), runnable -> new Thread(runnable,
                blockingHttpSchedulerProps.getThreadName()));
    }

    @Bean
    @ConfigurationProperties(prefix = "blocking-http-scheduler")
    public BlockingHttpSchedulerProperties blockingHttpSchedulerProperties() {
        return new BlockingHttpSchedulerProperties();
    }

    @Getter
    @Setter
    private static class BlockingHttpSchedulerProperties {
        private String threadName;
        private int numOfThreads;
    }
}
