package cn.panda.ronda.base.remoting.exchange;

import java.net.InetSocketAddress;

public interface ServerChannel {

    /**
     * @return channel的地址
     */
    InetSocketAddress getAddress();

    /**
     * 开始
     */
    void start();

    /**
     * 停止
     */
    void close();

}
