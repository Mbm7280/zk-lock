package com.echo.zk.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TaskService {
    @Autowired
    private ZkTemporaryOrderNodeLock zkTemporaryOrderNodeLock;

    @RequestMapping("/testLock")
    public String testLock() {
        try {
            zkTemporaryOrderNodeLock.getLock();
            log.info("获取锁成功 执行业务逻辑");
            zkTemporaryOrderNodeLock.unLock();
        } catch (Exception e) {
            zkTemporaryOrderNodeLock.unLock();
        }
        return "ok";
    }
}
