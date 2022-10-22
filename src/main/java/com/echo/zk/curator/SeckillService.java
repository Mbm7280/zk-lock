package com.echo.zk.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 秒杀场景
 * 使用Curator框架 分布式锁
 */
@RestController
@Slf4j
public class SeckillService {

    @Autowired
    private CuratorZkLock curatorZkLock;

    // ZooKeeper 锁节点路径, 分布式锁的相关操作都是在这个节点上进行
    private final String lockPath = "/distributed-lock";

    @RequestMapping("/seckillLock")
    public boolean seckillLock () throws Exception {
        InterProcessMutex interProcessMutex = null;
        boolean result = false;
        try {
            // 创建zk客户端连接
            CuratorFramework client = curatorZkLock.getClient();
            // 获取锁 底层 zk上创建一个临时顺序编号节点
            interProcessMutex = new InterProcessMutex(client, lockPath);
            //获取锁 一直没有获取锁成功的情况下，默认是会阻塞。
            interProcessMutex.acquire();
            log.info("<获取锁成功....>");
            // 模拟业务逻辑执行
            log.info("<执行秒杀扣库存业务逻辑....>");
            result = true;
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            if(interProcessMutex != null) {
                interProcessMutex.release();
            }
        }
    }

}
