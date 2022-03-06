package com.vassilis.library.reactive.filter;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.vassilis.library.reactive.contex.CloudContext;
import com.vassilis.library.reactive.contex.CloudContextHolder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import static com.vassilis.library.reactive.configuration.ReactorCloudContextConfiguration.CC_CONTEXT_IN_REACTOR_CONTEXT_KEY;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Component
@Order(1)
@Slf4j
public class CloudContextFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        var context = createCloudContext(exchange.getRequest());
        CloudContextHolder.set(context);

        return chain.filter(exchange)
                .contextWrite(originalCtx -> Context.of(CC_CONTEXT_IN_REACTOR_CONTEXT_KEY, context))
                .doAfterTerminate(CloudContextHolder::remove);
    }

    private CloudContext createCloudContext(ServerHttpRequest request) {
        CloudContext context = new CloudContext();
        context.setRandomTransactionId();

        HttpHeaders headers = request.getHeaders();
        keepTransactionIdIfExists(context, headers);

        context.setCustomerId(headers.getFirst("CustomerId"));

        return context;
    }

    private void keepTransactionIdIfExists(CloudContext context, HttpHeaders headers) {
        String transactionId = firstNonNull(
                headers.getFirst("TransactionId"),
                headers.getFirst("Cws-TransactionId")
        );

        if (isNotEmpty(transactionId)) {
            context.setTransactionId(transactionId);
        }
    }
}
