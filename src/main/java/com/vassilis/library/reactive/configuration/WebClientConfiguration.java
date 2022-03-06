package com.vassilis.library.reactive.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.vassilis.library.reactive.contex.CloudContextHolder;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient nettyWebClient(WebClient.Builder builder,
                                    ExchangeFilterFunction cloudContextPropagatorFilter,
                                    ExchangeFilterFunction monitoringFilter) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);

        return builder
                .filter(cloudContextPropagatorFilter)
                .filter(monitoringFilter)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public ExchangeFilterFunction cloudContextPropagatorFilter() {
        return ((request, next) -> {
            log.info("Thread is seeing the CloudContext {}", CloudContextHolder.get());

            ClientRequest.Builder requestBuilder = ClientRequest.from(request);
            requestBuilder.header("TransactionId", CloudContextHolder.get().getTransactionId());

            return next.exchange(requestBuilder.build());
        });
    }

    @Bean
    public ExchangeFilterFunction monitoringFilter() {
        return ((request, next) -> next.exchange(request)
                .doOnSubscribe(ignored ->
                        log.info("http request to method = {}, uri = {} started...",
                                request.method(),
                                request.url()))
                .elapsed()
                .map(response -> {
                    log.info("http request to method = {}, uri = {} completed with status {} in {}ms...",
                            request.method(),
                            request.url(),
                            response.getT2().statusCode(),
                            response.getT1());
                    return response.getT2();
                }));
    }
}
