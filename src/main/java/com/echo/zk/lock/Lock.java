package com.echo.zk.lock;

public interface Lock {
    /**
     * 获取锁
     */
    void getLock();

    /**
     * 释放锁
     */
    void unLock();
}
