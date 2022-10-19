package com.echo.zk;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

/**
 * Zk方法：
 *      节点监控
 */
public class ZkTest02 {
    private static ZkClient zkClient = new ZkClient("localhost:2181", 5000);

    public static void main(String[] args) {
        zkClient.subscribeDataChanges("/echoTemp01", new IZkDataListener() {

            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                System.out.println("s:" + s + "," + o);
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                System.out.println("del:" + s);
            }
        });
        while (true) {
        }
    }

}
