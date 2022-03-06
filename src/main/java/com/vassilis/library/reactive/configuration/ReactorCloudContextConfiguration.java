package com.vassilis.library.reactive.configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.context.annotation.Configuration;

import com.vassilis.library.reactive.contex.CloudContext;
import com.vassilis.library.reactive.contex.CloudContextHolder;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

/**
 * The configuration of Reactor which puts the instance of {@link com.vassilis.library.reactive.contex.CloudContext}
 * from {@link com.vassilis.library.reactive.contex.CloudContextHolder}
 * in to the {@link Context reactor context} when ever the {@link Flux} or {@link Mono} is created.
 * <p>
 * The configuration also lifts the {@link CloudContext} from {@link Context reactor context} in to {@link CloudContext}
 * when ever reactor operator is applied
 *
 * @see Hooks#onEachOperator
 * @see CloudContextHolder
 * @see CloudContext
 * @see Flux#contextWrite(ContextView)
 * @see Mono#contextWrite(ContextView)
 */
@Configuration
public class ReactorCloudContextConfiguration {

    private static final String CC_CONTEXT_LIFTER_HOOK_KEY = "CC_CONTEXT_LIFTER_HOOK_KEY";
    private static final String CC_CONTEXT_SUBSCRIBER_HOOK_KEY = "CC_CONTEXT_SUBSCRIBER_HOOK_KEY";

    public static final String CC_CONTEXT_IN_REACTOR_CONTEXT_KEY = "ccContext";
    public static final String CC_CONTEXT_THREAD_IN_REACTOR_CONTEXT_KEY = "ccContextThread";

    @PostConstruct
    public void contextOperatorHook() {
        /*
        The ccContext subscriber hook must be applied before lifter hook
        On last operator hooked as well last operator is not included in each operator.
        On each operator mean each except the last.
        Enriching the publisher with context is done only in the onLast hook because the context is subscription
        bounded - there is no point in applying it on each next operator.
         */
        Hooks.onLastOperator(
                CC_CONTEXT_SUBSCRIBER_HOOK_KEY,
                this::enrichReactiveContextWithCcContext);

        Hooks.onEachOperator(
                CC_CONTEXT_LIFTER_HOOK_KEY,
                Operators.lift((scannable, subscriber) -> new CcContextLifter(subscriber)));
    }

    private Publisher<Object> enrichReactiveContextWithCcContext(Publisher<Object> publisher) {
        var cloudContext = CloudContextHolder.get();
        String threadName = Thread.currentThread().getName();
        Context context = Context.of(
                CC_CONTEXT_IN_REACTOR_CONTEXT_KEY, cloudContext,
                CC_CONTEXT_THREAD_IN_REACTOR_CONTEXT_KEY, threadName
        );

        if (publisher instanceof Flux) {
            return Flux.from(publisher)
                    .contextWrite(existingContext -> mergeWithExistingContext(context, existingContext));
        } else if (publisher instanceof Mono) {
            return Mono.from(publisher)
                    .contextWrite(existingContext -> mergeWithExistingContext(context, existingContext));
        }

        return publisher;
    }

    private Context mergeWithExistingContext(ContextView context, Context existingContext) {
        if (!existingContext.hasKey(CC_CONTEXT_IN_REACTOR_CONTEXT_KEY)) {
            return existingContext.putAll(context);
        }
        return existingContext;
    }

    @PreDestroy
    public void cleanupHook() {
        Hooks.resetOnEachOperator(CC_CONTEXT_LIFTER_HOOK_KEY);
        Hooks.resetOnLastOperator(CC_CONTEXT_SUBSCRIBER_HOOK_KEY);
    }

    /**
     * Lifts the {@link CloudContext} from {@link Context reactor context} in to {@link CloudContextHolder}
     * when ever reactor operator is applied.
     * <p>
     * The lifter is applied as a reactor hook. It ensures the {@link CloudContext} is available in
     * {@link CloudContextHolder}
     * for example for {@link com.citrix.commons.logging.log4j.SplunkLayout} within reactor operator callback.
     *
     * @param <T> the {@link Subscriber} data type
     */
    private static class CcContextLifter<T> implements CoreSubscriber<T> {

        private final CoreSubscriber<T> wrappedSubscriber;

        public CcContextLifter(CoreSubscriber<T> wrappedSubscriber) {
            this.wrappedSubscriber = wrappedSubscriber;
        }

        @Override
        public Context currentContext() {
            return wrappedSubscriber.currentContext();
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            liftCcContextToCcContextHolder();
            wrappedSubscriber.onSubscribe(subscription);
        }

        @Override
        public void onNext(T t) {
            liftCcContextToCcContextHolder();
            wrappedSubscriber.onNext(t);
        }

        @Override
        public void onError(Throwable throwable) {
            liftCcContextToCcContextHolder();
            wrappedSubscriber.onError(throwable);
        }

        @Override
        public void onComplete() {
            liftCcContextToCcContextHolder();
            wrappedSubscriber.onComplete();
        }

        private void liftCcContextToCcContextHolder() {
            CloudContext ccContext = findCcContextInReactorContext(currentContext());
            if (ccContext != null) {
                CloudContextHolder.set(ccContext);
            }
            // Do not clear the CC Context in case this is the same thread as the one which created/subscribed
            // this publisher.
        }

        private CloudContext findCcContextInReactorContext(Context context) {
            if (context.hasKey(CC_CONTEXT_IN_REACTOR_CONTEXT_KEY)) {
                Object entry = context.get(CC_CONTEXT_IN_REACTOR_CONTEXT_KEY);
                if (entry instanceof CloudContext) {
                    return (CloudContext) entry;
                }
            }
            return null;
        }
    }
}
