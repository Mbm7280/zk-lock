package com.echo.zk.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

/**
 * 模拟分布式环境，手动创建一个lockpath
 */
public class createLock {

    public static void main(String[] args) throws Exception {
        CuratorZkLock curatorZkLock = new CuratorZkLock();
        CuratorFramework client = curatorZkLock.getClient();
        InterProcessMutex interProcessMutex = new InterProcessMutex(client, "/distributed-lock");
        interProcessMutex.acquire();
        while (true) {
        }
    }

}
