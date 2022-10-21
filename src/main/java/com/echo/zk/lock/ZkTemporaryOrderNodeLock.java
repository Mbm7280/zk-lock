package com.echo.zk.lock;

import com.echo.zk.utiils.LockContext;
import com.echo.zk.utiils.ZkClientUtils;
import org.I0Itec.zkclient.IZkDataListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 分布式锁解决羊群效应
 */
@Component
public class ZkTemporaryOrderNodeLock extends ZookeeperAbstractLock{

    /**
     * 分布式锁 路径
     */
    private String lockParent = "/lock";
    /**
     * 临时顺序编号节点
     */
    private String lockPath = "/lockPath";

    // 上一个临时顺序编号节点
    private String prevLockPath;

    private CountDownLatch countDownLatch;

    @Override
    protected boolean tryLock() {
        // 1.创建一个临时顺序编号节点
        String tempNodeName = LockContext.get();
        if(StringUtils.isEmpty(tempNodeName)) {
            ZkClientUtils.newZkClient().createEphemeralSequential(lockParent + lockPath,"lock");
            LockContext.set(tempNodeName);
        }

        // 2.查询到当前根节点下所有的子节点 实现排序
        List<String> childrens = ZkClientUtils.getZkClient().getChildren(lockParent);
        Collections.sort(childrens);

        // 3.查找到最小的节点 则代表获取锁成功
        if(tempNodeName.equals(lockParent + "/" + childrens.get(0))) {
            return true;
        }

        // 4. 如果不是最小的节点，则表示获取锁失败。 查找到上一个节点
        int index = Collections.binarySearch(childrens, tempNodeName.substring(lockParent.length() + 1));
        prevLockPath = lockParent + "/" + childrens.get(index - 1);
        return false;
    }

    @Override
    protected void waitLock() {
        // 创建一个事件监听
        IZkDataListener iZkDataListener = new IZkDataListener() {

            @Override
            public void handleDataChange(String s, Object o) throws Exception {
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                //当我们节点被删除之后，我们应该从新被唤醒
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }

            }
        };
        // 监听上一个节点
        ZkClientUtils.getZkClient().subscribeDataChanges(prevLockPath, iZkDataListener);
        try {
            if (ZkClientUtils.getZkClient().exists(prevLockPath)) {
                //让当前线程阻塞
                countDownLatch = new CountDownLatch(1);
                countDownLatch.await();
            }

        } catch (Exception e) {
        }
        // 当我们节点被删除之后，我们应该从新被唤醒 移除事件监听
        ZkClientUtils.getZkClient().unsubscribeDataChanges(prevLockPath, iZkDataListener);
    }
}
