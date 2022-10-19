package com.echo.zk.lock;

import com.echo.zk.utiils.ZkClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j

public abstract class ZookeeperAbstractLock implements Lock{

    @Value("${server.port}")
    private String serverPort;

    protected abstract void waitLock();

    protected abstract boolean tryLock();

    @Override
    public void getLock(){
        for (int i = 0; i < 5; i++) {
            boolean tryLock = tryLock();
            if(tryLock) {
                log.info(">{}：服务获取成功<",serverPort);
                return;
            }
        }
        // 重试5初次还是失败，则开始阻塞。
        waitLock();
        // 被唤醒后 从新获取锁
        getLock();
    }

    @Override
    public void unLock(){
        ZkClientUtils.getZkClient().close();
        log.info(">>[{}]服务释放了锁<<<", serverPort);
    }

}
