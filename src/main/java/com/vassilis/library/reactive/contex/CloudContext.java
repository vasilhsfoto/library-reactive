package com.vassilis.library.reactive.contex;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.Data;

/**
 * Cloud context wraps information necessary for logging and request tracking (using Transaction ID).
 * <p>
 * The Transaction ID must be propagated to all outbound calls
 * <p>
 * CloudContext is not strictly bound to HTTP request. It is expected to be used also for message queues, asynchronous
 * processing and background tasks.
 * <p>
 */
@Data
public class CloudContext {
    private String customerId;
    private String transactionId;

    private String callerIp;
    private String category;

    // HTTP request context
    private String requestMethod;
    private String domain;
    private String uriPath;
    private String uriQuery;
    private String refererHeader;
    private String acceptLanguage;
    private String citrixLocale;

    // CC Auth context
    private String callerServiceName;
    private String callerServiceInstanceId;
    private String userId;
    private String userPrincipal;
    private String serviceProfile;

    private Map<String, Object> customProperties = new HashMap<>();

    public void setRandomTransactionId() {
        transactionId = UUID.randomUUID().toString();
    }
}
