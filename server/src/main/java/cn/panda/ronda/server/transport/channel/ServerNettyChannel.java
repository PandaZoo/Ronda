package cn.panda.ronda.server.transport.channel;

import cn.panda.ronda.base.remoting.codec.CodecInterface;
import cn.panda.ronda.base.remoting.codec.CodecTypeEnum;
import cn.panda.ronda.base.remoting.exception.ExceptionCode;
import cn.panda.ronda.base.remoting.exception.RondaException;
import cn.panda.ronda.base.remoting.exchange.ServerChannel;
import cn.panda.ronda.base.remoting.helper.EnumFunction;
import cn.panda.ronda.server.codec.MessageDecoder;
import cn.panda.ronda.server.codec.MessageEncoder;
import cn.panda.ronda.server.transport.config.URL;
import cn.panda.ronda.server.transport.handler.NettyHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * ServerNettyChannel
 */
public class ServerNettyChannel implements ServerChannel {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private URL url;
    private Boolean isConnected = Boolean.FALSE;
    private ServerBootstrap serverBootstrap = new ServerBootstrap();

    public ServerNettyChannel(URL url) {
        this.url = url;
    }

    @Override
    public InetSocketAddress getAddress() {
        return new InetSocketAddress(this.url.getAddress(), this.url.getPort());
    }

    @Override
    public void start() {
        if (!this.isConnected) {
            // 根据transportConfig获取对应的codec
            this.bossGroup = new NioEventLoopGroup();
            this.workerGroup = new NioEventLoopGroup();
            String protocol =  this.url.getProtocol();
            Optional<CodecTypeEnum> optional = EnumFunction.findByKey(CodecTypeEnum.class, Integer.parseInt(protocol));
            if (!optional.isPresent()) {
                throw new RondaException(ExceptionCode.PROTOCOL_NOT_FOUND);
            }

            CodecInterface codecByType = CodecTypeEnum.getCodecByType(optional.get());

            this.bossGroup = new NioEventLoopGroup();
            this.workerGroup = new NioEventLoopGroup();
            this.serverBootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new MessageDecoder(codecByType), new MessageEncoder(codecByType), new NettyHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            try {
                ChannelFuture channelFuture = serverBootstrap.bind(this.url.getPort()).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.isConnected = true;
        }
    }

    @Override
    public void close() {
        this.isConnected = false;
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }
}
