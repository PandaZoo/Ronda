package cn.panda.ronda.client.util;

import cn.panda.ronda.base.remoting.codec.JsonCodec;
import cn.panda.ronda.client.codec.MessageDecoder;
import cn.panda.ronda.client.codec.MessageEncoder;
import cn.panda.ronda.client.transport.config.ChannelConfig;
import cn.panda.ronda.client.transport.handler.NettyHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;


/**
 * netty util
 * todo 把各种channel对应的Bootstrap缓存下来?
 *
 * created by yongkang.zhang
 * added at 2018/4/10
 */
@Slf4j
public class NettyUtil {

    private static EventLoop EVENT_LOOP;
    private static Bootstrap BOOTSTRAP;
    private static EventLoopGroup BOSS_GROUP = new NioEventLoopGroup();

    public static Channel getChannel(ChannelConfig transportConfig) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(BOSS_GROUP)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    // todo 根据不同协议进行不同的encode decode
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast(new MessageDecoder(new JsonCodec()), new MessageEncoder(new JsonCodec()), new NettyHandler());
                    }
                });

        try {
            ChannelFuture channelFuture = bootstrap.connect(transportConfig.getRemoteIp(), transportConfig.getRemotePort()).sync();
            return channelFuture.channel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
