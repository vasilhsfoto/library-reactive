package com.vassilis.library.reactive.client;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.vassilis.library.reactive.configuration.property.CoinCapClientProperties;
import com.vassilis.library.reactive.representation.RateDto;
import com.vassilis.library.reactive.representation.RatesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoinCapClient {
    private final WebClient nettyWebClient;
    private final CoinCapClientProperties coinCapClientProperties;

    public Flux<RateDto> getRates() {
        Flux<RateDto> response = nettyWebClient.get()
                .uri(UriComponentsBuilder.fromUriString(coinCapClientProperties.getUrl())
                        .pathSegment("v2/rates")
                        .build()
                        .toUri())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(RatesDto.class)
                .map(RatesDto::getData)
                .flatMapMany(Flux::fromIterable);

        return response;
    }
}
