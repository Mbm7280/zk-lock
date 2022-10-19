package com.echo.zk.utiils;

import org.I0Itec.zkclient.ZkClient;

/**
 * Zk 连接工具类
 */
public class ZkClientUtils {
    private static ZkClient zkClient = null;

    public static ZkClient getZkClient() {
        return zkClient;
    }

    public static ZkClient newZkClient() {
        if (zkClient != null) {
            zkClient.close();
        }
        zkClient = new ZkClient("localhost:2181",3000,3000);
        return zkClient;
    }
}
