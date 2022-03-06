package com.vassilis.library.reactive.contex;

public final class CloudContextHolder {

    private static ThreadLocal<CloudContext> threadLocal = ThreadLocal.withInitial(CloudContext::new);

    public static CloudContext get() {
        return threadLocal.get();
    }

    public static void set(CloudContext context) {
        if (context == null) {
            throw new IllegalArgumentException("You must not set context to null value use 'remove()' method instead");
        }
        threadLocal.set(context);
    }

    public static void remove() {
        threadLocal.remove();
    }
}
