package com.vassilis.library.reactive.controller;

import com.vassilis.library.reactive.exception.WebAppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * what happens if we want to define our Error Domain Object plus logging of requests ?
 * Here is an attempt to define our own error properties
 * The Logging in Web flux happens nicely inside the AbstractWebExceptionHandler.logError(ServerRequest request, ServerResponse response, Throwable throwable)
 */
@Component
@Slf4j
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        final var error = super.getError(request);
        log.debug("Caught an error {}", error);
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, includeStackTrace);

        Map<String, Object> customErrorAttributes = new LinkedHashMap<>();
        customErrorAttributes.put("message", errorAttributes.get("message"));
        customErrorAttributes.put("status", errorAttributes.get("status"));

        if (error instanceof WebAppException) {
            WebAppException webAppException = (WebAppException) error;
            customErrorAttributes.replace("message", webAppException.getMessage());
            customErrorAttributes.replace("status", webAppException.getHttpStatus().value());
        }

        return customErrorAttributes;
    }

}
