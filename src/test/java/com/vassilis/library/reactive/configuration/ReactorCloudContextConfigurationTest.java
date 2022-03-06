package com.vassilis.library.reactive.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.vassilis.library.reactive.contex.CloudContext;
import com.vassilis.library.reactive.contex.CloudContextHolder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.test.StepVerifier;

import static com.vassilis.library.reactive.configuration.ReactorCloudContextConfiguration.CC_CONTEXT_IN_REACTOR_CONTEXT_KEY;

@ActiveProfiles(profiles = "test")
@SpringBootTest
class ReactorCloudContextConfigurationTest {

    @Autowired
    private Scheduler blockingHttpScheduler;

    @DisplayName("When something exists in thread local, then the hook 'last operator' will copy it to the Reactive " +
            "Context and propagates it to all stream operators")
    @Test
    public void testLastOperatorHookIsCalled() {
        var cloudContext = new CloudContext();
        cloudContext.setTransactionId("transaction1");
        CloudContextHolder.set(cloudContext);

        Mono<String> mono = Mono.just("Alex")
                .flatMap(name -> Mono.deferContextual(contextView -> {
                    var cc = (CloudContext) contextView.get(CC_CONTEXT_IN_REACTOR_CONTEXT_KEY);
                    return Mono.just(name + " is consumed with cloud context " + cc.getTransactionId());
                }));

        StepVerifier.create(mono)
                .expectNext("Alex is consumed with cloud context transaction1")
                .verifyComplete();
    }

    @DisplayName("When something exists in thread local, then the hook 'last operator' will copy it to the Reactive " +
            "and then the hook `each operator` will the cloud context from Reactive Context to Thread local")
    @Test
    public void testEachOperatorHookIsCalled() {
        var cloudContext = new CloudContext();
        cloudContext.setTransactionId("transaction2");
        CloudContextHolder.set(cloudContext);

        Mono<String> mono = Mono.just("Alex")
                .map(name -> {
                    var cc = CloudContextHolder.get();
                    return name + " is consumed with cloud context " + cc.getTransactionId();
                })
                .subscribeOn(blockingHttpScheduler);

        StepVerifier.create(mono)
                .expectNext("Alex is consumed with cloud context transaction2")
                .verifyComplete();
    }

}