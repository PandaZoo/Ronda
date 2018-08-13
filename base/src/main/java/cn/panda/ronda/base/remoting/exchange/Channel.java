package cn.panda.ronda.base.remoting.exchange;

import cn.panda.ronda.base.remoting.message.RequestMessage;
import cn.panda.ronda.base.remoting.message.ResponseMessage;

import java.net.InetSocketAddress;

/**
 * exchange channel
 * todo 考虑增加async
 * created by yongkang.zhang
 * added at 2018/4/9
 */
public interface Channel {

    InetSocketAddress getRemoteAddress();

    /**
     * 需要host和port
     * @param url host和port
     */
    void connect(String url);

    Boolean isConnected();

    /**
     * @param url host和port
     */
    void reconnect(String url);

    void close();

    ResponseMessage sendRequest(RequestMessage requestMessage);

}
