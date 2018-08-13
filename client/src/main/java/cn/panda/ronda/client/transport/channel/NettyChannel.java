package cn.panda.ronda.client.transport.channel;

import cn.panda.ronda.base.remoting.codec.CodecInterface;
import cn.panda.ronda.base.remoting.codec.CodecTypeEnum;
import cn.panda.ronda.base.remoting.exception.BaseException;
import cn.panda.ronda.base.remoting.exception.ExceptionCode;
import cn.panda.ronda.base.remoting.exchange.Channel;
import cn.panda.ronda.base.remoting.helper.EnumFunction;
import cn.panda.ronda.base.remoting.message.RequestMessage;
import cn.panda.ronda.base.remoting.message.ResponseMessage;
import cn.panda.ronda.client.codec.MessageDecoder;
import cn.panda.ronda.client.codec.MessageEncoder;
import cn.panda.ronda.client.transport.config.TransportConfig;
import cn.panda.ronda.client.transport.handler.NettyHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * netty channel
 *
 * 现在应该是一个service 一个channel
 * created by yongkang.zhang
 * added at 2018/4/9
 */
@Slf4j
public class NettyChannel implements Channel {

    private Bootstrap bootstrap = new Bootstrap();
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private io.netty.channel.Channel channel;
    private Boolean isConnected = false;
    private TransportConfig transportConfig;

    public NettyChannel(TransportConfig transportConfig) {
        this.transportConfig = transportConfig;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return new InetSocketAddress(this.transportConfig.getRemoteIp(), this.transportConfig.getRemotePort());
    }

    @Override
    public void connect(String url) {
        if (this.isConnected) {
            return;
        }

        Optional<CodecTypeEnum> optionalCodecTypeEnum = EnumFunction.findByKey(CodecTypeEnum.class, Integer.parseInt(transportConfig.getProtocol()));
        if (!optionalCodecTypeEnum.isPresent()) {
            throw new BaseException(ExceptionCode.PROTOCOL_NOT_FOUND);
        }
        CodecInterface codecInterface = CodecTypeEnum.getCodecByType(optionalCodecTypeEnum.get());


        bootstrap.group(bossGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast(new MessageEncoder(codecInterface), new MessageDecoder(codecInterface), new NettyHandler());
                    }
                });
        try {
            ChannelFuture f = bootstrap.connect(this.transportConfig.getRemoteIp(), this.transportConfig.getRemotePort()).sync();
            this.channel = f.channel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.isConnected = true;
    }

    @Override
    public void reconnect(String url) {
        connect(url);
    }

    @Override
    public void close() {
        try {
            bossGroup.shutdownGracefully();
        } catch (Exception e) {
            log.error("shutdown channel error", e);
        } finally {
            this.isConnected = false;
        }
    }

    @Override
    public ResponseMessage sendRequest(RequestMessage requestMessage) {
        ChannelFuture future = this.channel.writeAndFlush(requestMessage);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean isConnected() {
        return this.isConnected;
    }
}
