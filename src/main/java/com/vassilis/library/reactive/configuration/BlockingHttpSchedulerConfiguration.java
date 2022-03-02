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
     * To be used for overcoming the blocking of a thread
     * @param blockingHttpSchedulerProps
     * @return
     */
    @Bean
    public Scheduler blockingHttpScheduler(BlockingHttpSchedulerProperties blockingHttpSchedulerProps) {

        return Schedulers.newParallel(
                blockingHttpSchedulerProps.getThreadName(),
                blockingHttpSchedulerProps.getNumOfThreads(),
                true);
    }

    @Bean
    @ConfigurationProperties(prefix = "proxy.blocking-http-scheduler")
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
