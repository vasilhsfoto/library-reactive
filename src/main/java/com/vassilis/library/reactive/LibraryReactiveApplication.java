package com.vassilis.library.reactive;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@SpringBootApplication
@Slf4j
public class LibraryReactiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryReactiveApplication.class, args);
	}

	@Bean
	@Order(1)
	CommandLineRunner init() {
		return args -> {
			log.info("1st call back after spring container is up...");
		};
	}
}