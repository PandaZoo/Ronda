package cn.panda.ronda.client.transport.client;

import cn.panda.ronda.base.remoting.codec.CodecTypeEnum;
import cn.panda.ronda.base.remoting.exception.BaseException;
import cn.panda.ronda.base.remoting.exception.ExceptionCode;
import cn.panda.ronda.base.remoting.exchange.Channel;
import cn.panda.ronda.base.remoting.exchange.ExchangeClient;
import cn.panda.ronda.base.remoting.message.RequestMessage;
import cn.panda.ronda.base.remoting.message.ResponseMessage;
import cn.panda.ronda.client.demo.HelloService;
import cn.panda.ronda.client.transport.MessageFuture;
import cn.panda.ronda.client.transport.channel.NettyChannel;
import cn.panda.ronda.client.transport.config.TransportConfig;
import cn.panda.ronda.client.util.NettyUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * ronda client
 * 用future的功能来做超时
 * 1. 需要记录哪些service配置哪些协议
 * 2. 注册中心告诉客户端 哪些URL上有哪些service
 * 客户端根据service和协议判断可以连接的remote server
 * 需要根据xml解析的进行注册，然后根据service选择任意一个可用的remote channel进行调用
 *
 * created by yongkang.zhang
 * added at 2018/4/9
 */
@Slf4j
public class RondaClient implements ExchangeClient {

    private Channel channel;

    public static final ConcurrentHashMap<Long, MessageFuture<ResponseMessage>> RESPONSE_MAP = new ConcurrentHashMap<>();

    private static final HashMap<TransportConfig, Channel> remoteMap = new HashMap<>();

    static {
        TransportConfig transportConfig = new TransportConfig();
        transportConfig.setProtocol(String.valueOf(CodecTypeEnum.HESSIAN.getCode()));
        transportConfig.setRemoteIp("127.0.0.1");
        transportConfig.setRemotePort(22000);
        transportConfig.setServiceClass(HelloService.class);
        NettyChannel nettyChannel = new NettyChannel(transportConfig);
        nettyChannel.connect(null);
        remoteMap.put(transportConfig, nettyChannel);
    }

    @Override
    public ResponseMessage invoke(RequestMessage requestMessage) {
        this.channel = getChannel(requestMessage);
        this.channel.sendRequest(requestMessage);

        MessageFuture<ResponseMessage> msgFuture = new MessageFuture<>(this.channel, 5);
        // 在开始的时候加入一个新的reponse message，然后在获取到信息的时候写入对应的response
        MessageFuture<ResponseMessage> messageFuture = new MessageFuture<>(this.channel, 5);
        RESPONSE_MAP.put(requestMessage.getMessageId(), messageFuture);
        try {
            return messageFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("invoke failed, method is: {}", requestMessage.getTargetMethod(), e);
            throw new RuntimeException(e);
        }
    }

    private Channel getChannel(RequestMessage requestMessage) {
        Optional<Channel> channelOptional = remoteMap.entrySet()
                .stream()
                .filter(e -> Objects.equals(e.getKey().getServiceClass().getName(), requestMessage.getTargetClass()))
                .map(Map.Entry::getValue)
                .findFirst();
        if (channelOptional.isPresent()) {
            return channelOptional.get();
        } else {
            throw new BaseException(ExceptionCode.CHANNEL_NOT_FOUND);
        }

    }


    @Override
    public InetSocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public void connect(String url) {
        // Transport config
        TransportConfig config = new TransportConfig();
        config.setProtocol("ronda");
        config.setRemoteIp("127.0.0.1");
        config.setRemotePort(8080);

    }

    @Override
    public void reconnect(String url) {

    }

    @Override
    public void close() {

    }

    @Override
    public ResponseMessage sendRequest(RequestMessage requestMessage) {
        return null;
    }
}
