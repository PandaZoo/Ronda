package cn.panda.ronda.server.transport.server;

import cn.panda.ronda.base.remoting.codec.CodecTypeEnum;
import cn.panda.ronda.base.remoting.exchange.Channel;
import cn.panda.ronda.base.remoting.exchange.ServerChannel;
import cn.panda.ronda.server.transport.channel.ServerNettyChannel;
import cn.panda.ronda.server.transport.config.URL;

import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

/**
 * RondaServer
 * 1. 提供channel供客户端连接
 * 2. 读取客户端的内容
 * 3. 进行返回
 */
@Slf4j
public class RondaServer {

    private Channel channel;

    private static Map<URL, ServerChannel> channelMap = new HashMap<>();

    public void startServer() {
        URL url = testUrl();
        ServerNettyChannel serverNettyChannel = new ServerNettyChannel(url);
        serverNettyChannel.start();
        channelMap.put(url, serverNettyChannel);
    }


    public static void main(String[] args) throws InterruptedException {
        RondaServer rondaServer = new RondaServer();
        rondaServer.startServer();
        Thread.sleep(5 * 1000 * 60);
    }

    private URL testUrl() {
        URL url = new URL();
        url.setAddress("127.0.0.1");
        url.setPort(22000);
        url.setProtocol(CodecTypeEnum.HESSIAN.getCode().toString());
        return url;
    }

    public static void putChannelMap(URL url, ServerChannel serverChannel) {
        channelMap.put(url, serverChannel);
    }

    public static URL getChannelMap(CodecTypeEnum codecTypeEnum) {
        for (Map.Entry<URL, ServerChannel> entry : channelMap.entrySet()) {
            if (Integer.parseInt(entry.getKey().getProtocol()) == (codecTypeEnum.getCode())) {
                return entry.getKey();
            }
        }

        return null;
    }

}
