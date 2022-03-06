package com.vassilis.library.reactive;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class LibraryReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryReactiveApplication.class, args);
    }

    @Bean
    @Order(1)
    CommandLineRunner init() {
        return args -> log.info("1st call back after spring container is up...");
    }
}