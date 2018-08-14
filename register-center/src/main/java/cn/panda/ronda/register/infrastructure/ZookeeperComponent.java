package cn.panda.ronda.register.infrastructure;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author yongkang.zhang
 * created at 13/08/2018
 */
public class ZookeeperComponent implements Watcher {

    private ZooKeeper zooKeeper;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private static final String PARENT_DIR = "/ronda";

    private static final String SEPARATOR = "/";

    private String host;

    public ZookeeperComponent(String host) {
        this.host = host;
    }

    /**
     * 在connect之后，进行数据目录的初始化
     */
    public void init() throws Exception {
        Stat stat = zooKeeper.exists(PARENT_DIR, false);
        if (stat == null) {
            createNode(PARENT_DIR, "ronda register");
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
            System.out.println("Watch received event!");
            countDownLatch.countDown();
        }
    }

    /**
     * session timeout这里指的是client连接失败之后，zookeeper 集群自动重连的时限。
     * 如果在这个时限之内，failover成功的话连接状态就变成CONNECTED，否则连接状态为变成SESSION_EXPIRED
     */
    public void connectZookeeper() throws Exception {
        int SESSION_TIME_OUT = 5000;
        zooKeeper = new ZooKeeper(this.host, SESSION_TIME_OUT, this);
        countDownLatch.await();
        System.out.println("zookeeper connection success");
    }

    private String createNode(String path, String data) throws Exception {
        return this.zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    private List<String> getChildren(String path) throws KeeperException, InterruptedException {
        return zooKeeper.getChildren(path, false);
    }

    public String getData(String path) throws KeeperException, InterruptedException {
        path = PARENT_DIR.concat(SEPARATOR).concat(path);

        byte[] data = zooKeeper.getData(path, false, null);
        if (data == null) {
            return "";
        }

        return new String(data);
    }


    public Stat setData(String path, String data) throws Exception {
        path = PARENT_DIR.concat(SEPARATOR).concat(path);

        if (zooKeeper.exists(path, true) == null) {
            createNode(path, data);
        }

        return zooKeeper.setData(path, data.getBytes(), -1);
    }

    public void deleteNode(String path) throws InterruptedException, KeeperException {
        path = PARENT_DIR.concat(SEPARATOR).concat(path);
        zooKeeper.delete(path, -1);
    }

    /**
     * 获取创建时间
     * @param path node
     */
    private long getCTime(String path) throws KeeperException, InterruptedException{
        Stat stat = zooKeeper.exists(path, false);
        return stat.getCtime();
    }

    /**
     * 获取某个路径下孩子的数量
     * @param path node
     */
    private Integer getChildrenNum(String path) throws KeeperException, InterruptedException{
        return zooKeeper.getChildren(path, false).size();
    }
    /**
     * 关闭连接
     */
    public void closeConnection() throws InterruptedException{
        if (zooKeeper != null) {
            zooKeeper.close();
        }
    }

}
