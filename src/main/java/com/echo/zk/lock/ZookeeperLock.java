package com.echo.zk.lock;

import com.echo.zk.utiils.ZkClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
public class ZookeeperLock extends ZookeeperAbstractLock{

    private String lockPath = "/lockPath";
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    @Value("${server.port}")
    private String serverPort;

    @Override
    protected boolean tryLock() {
        try{
            ZkClientUtils.newZkClient().createEphemeral(lockPath);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void waitLock() {
        // 注册一个监听事件
        IZkDataListener iZkDataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {

            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                log.info(">>[{}],其他节点已经释放锁<<", serverPort);
                countDownLatch.countDown();
            }
        };

        ZkClientUtils.getZkClient().subscribeDataChanges(lockPath,iZkDataListener);
        try {
            boolean exists = ZkClientUtils.getZkClient().exists(lockPath);
            if(exists){
                log.info(">>[{}],其他节点已经获取到锁，我要等待啦;<<", serverPort);
                countDownLatch.wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ZkClientUtils.getZkClient().unsubscribeDataChanges(lockPath, iZkDataListener);
    }


}
