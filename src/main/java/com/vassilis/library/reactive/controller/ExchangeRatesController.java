package com.vassilis.library.reactive.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vassilis.library.reactive.client.CoinCapClient;
import com.vassilis.library.reactive.contex.CloudContextHolder;
import com.vassilis.library.reactive.representation.RateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

@RestController
@RequestMapping("/api/exchange-rates")
@RequiredArgsConstructor
@Slf4j
public class ExchangeRatesController {

    private final CoinCapClient coinCapClient;
    private final Scheduler blockingHttpScheduler;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<RateDto> getRates() {
        log.info("Rate controller starts and CloudContext {}", CloudContextHolder.get());

        Flux<RateDto> flux = coinCapClient.getRates().subscribeOn(blockingHttpScheduler).log();

        log.info("Rate controller completes and CloudContext {}", CloudContextHolder.get());
        return flux;
    }
}
