package com.echo.zk.utiils;

/**
 * @ClassName LockContext
 **/
public class LockContext {

    private static ThreadLocal<String> threadLocal = new ThreadLocal<String>();

    public static void set(String lockNode) {
        threadLocal.set(lockNode);
    }

    public static String get() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }
}
