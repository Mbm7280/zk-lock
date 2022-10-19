package com.echo.zk.lock;

import com.echo.zk.entity.LockInfo;
import com.echo.zk.utiils.TranslationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class Task {

    /**
     * 存放当前jvm线程 获取到锁的信息key 锁的id  value 锁的信息
     */
    private static Map<String, LockInfo> lockInfoMap = new ConcurrentHashMap<>();
    private static final String STATE_START = "start";

    private static final String STATE_STOP = "stop";

    // 创建定时任务线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    @Value("{server.port}")
    private String serverPort;

//    @Autowired
//    private UserMapper userMapper;

    @Autowired
    private ZookeeperLock zookeeperLock;

    //    @Autowired
    private TranslationUtils translationUtils;

    public Task() {
        // 续命线程
        scheduledExecutorService.scheduleAtFixedRate(new DetectionAlgorithm(), 0, 5, TimeUnit.SECONDS);
    }

    class DetectionAlgorithm implements Runnable {
        @Override
        public void run() {
            lockInfoMap.forEach((k,lockInfo) -> {
                if(STATE_START.equals(lockInfo.getState())) {
                    // 1、主动关闭连接
                    lockInfo.getLock().unLock();
                    // 2、回滚
                    lockInfo.getTranslationUtils().rollback(lockInfo.getTransactionStatus());
                    // 3、阻塞线程停止
                    lockInfo.getLockThread().interrupt();;
                    // 4、避免重复检测
                    lockInfoMap.remove(k);
                }
            });
        }
    }



    /**
     * 每隔2s执行定时任务
     * @throws InterruptedException
     */
    @Scheduled(cron = "0/2 * * * * *")
    public void taskService() throws InterruptedException {
        try{
            // 获取锁
            zookeeperLock.getLock();
            // 开启事务
            TransactionStatus begin = translationUtils.begin();
            // 记录当先线程获取锁的信息
            String lockId = UUID.randomUUID().toString();
            lockInfoMap.put(lockId,new LockInfo(lockId,Thread.currentThread(),STATE_START,zookeeperLock,translationUtils,begin));

            // 业务代码运行
//            userMapper.insert("Echo",20);
            log.info("[{}]正在调用阿里云发送短信", serverPort);

            // 提交事务
            translationUtils.commit(begin);

            // 手动模拟代码运行超时
            Thread.sleep(5000000);

            // 释放锁
            zookeeperLock.unLock();
        }catch (Exception e){
            zookeeperLock.unLock();
         }
    }

}
