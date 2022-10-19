package com.echo.zk.entity;

import com.echo.zk.lock.Lock;
import com.echo.zk.utiils.TranslationUtils;
import org.springframework.transaction.TransactionStatus;

/**
 * 记录锁的信息
 */
public class LockInfo {

    // 锁的ID
    private String lockId;

    // 锁的线程
    private Thread lockThread;

    // 锁的状态 start 开始使用的状态 stop状态 锁释放了
    private String state;

    // 分布式 lock 锁
    private Lock lock;

    // 事务的状态
    private TransactionStatus transactionStatus;

    private TranslationUtils translationUtils;

    // 全参数构造
    public LockInfo(String lockId, Thread lockThread, String state, Lock lock, TranslationUtils translationUtils,
                    TransactionStatus transactionStatus) {
        this.lockId = lockId;
        this.lockThread = lockThread;
        this.state = state;
        this.lock = lock;
        this.translationUtils = translationUtils;
        this.transactionStatus = transactionStatus;
    }

    // 不含事务的构造
    public LockInfo(String lockId, Thread lockThread, String state, Lock lock) {
        this.lockId = lockId;
        this.lockThread = lockThread;
        this.state = state;
        this.lock = lock;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public Thread getLockThread() {
        return lockThread;
    }

    public void setLockThread(Thread lockThread) {
        this.lockThread = lockThread;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public TranslationUtils getTranslationUtils() {
        return translationUtils;
    }

    public void setTranslationUtils(TranslationUtils translationUtils) {
        this.translationUtils = translationUtils;
    }
}
