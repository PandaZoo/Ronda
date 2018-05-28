package cn.panda.ronda.client.transport.handler;

import cn.panda.ronda.base.remoting.message.ResponseMessage;
import cn.panda.ronda.client.transport.MessageFuture;
import cn.panda.ronda.client.transport.client.RondaClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyHandler extends SimpleChannelInboundHandler<ResponseMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ResponseMessage responseMessage) throws Exception {
        // 收到请求
        Long messageId = responseMessage.getMessageId();

        // 处理请求
        MessageFuture<ResponseMessage> messageFuture = RondaClient.RESPONSE_MAP.get(messageId);
        if (messageFuture != null) {
           messageFuture.setResult(responseMessage);
        } else {
            log.error("Message with id:{} not exists", messageId);
        }
    }
}
