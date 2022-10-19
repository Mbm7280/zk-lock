package com.echo.zk;

import org.I0Itec.zkclient.ZkClient;

/**
 * Java客户端连接 Zk
 *
 */
public class ZkTest01 {

    // 创建zk连接
    private static ZkClient zkClient = new ZkClient("localhost:2181",2000,2000);

    public static void main(String[] args) {
        // 创建一个临时节点
        zkClient.createEphemeral("/echoTemp01");

        try{
            Thread.sleep(80000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 创建一个永久节点
//        zkClient.createPersistent("/echoPert");

        // 关闭连接
        zkClient.close();

    }

}
