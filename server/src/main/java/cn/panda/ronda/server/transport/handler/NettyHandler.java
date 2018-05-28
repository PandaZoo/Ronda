package cn.panda.ronda.server.transport.handler;

import cn.panda.ronda.base.remoting.message.RequestMessage;
import cn.panda.ronda.base.remoting.message.ResponseMessage;
import cn.panda.ronda.server.util.ApplicationContextUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class NettyHandler extends SimpleChannelInboundHandler<RequestMessage> {



    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RequestMessage requestMessage) throws Exception {
        test(channelHandlerContext, requestMessage);
    }


    private void test(ChannelHandlerContext channelHandlerContext, RequestMessage requestMessage) throws Exception {
        log.info("接受到了消息: {}", requestMessage);
        ResponseMessage responseMessage = wrapResult(requestMessage, "测试结果");
        channelHandlerContext.writeAndFlush(responseMessage);
        log.info("返回了结果: {}", responseMessage);
    }

    private void formal(ChannelHandlerContext channelHandlerContext, RequestMessage requestMessage) throws Exception {
        // 1. 获取对应的service
        Object clazz = ApplicationContextUtil.findByClassName(requestMessage.getTargetClass());

        // 2. 执行方法
        String methodName = requestMessage.getTargetMethod();
        Method method = clazz.getClass().getMethod(methodName, requestMessage.getArgType());
        Object result = method.invoke(clazz, requestMessage.getArgs());

        // 3. 包装成responseMessage写回去
        ResponseMessage responseMessage = wrapResult(requestMessage, result);

        // 4. 将结果写入回去
        channelHandlerContext.writeAndFlush(responseMessage);
    }

    private ResponseMessage wrapResult(RequestMessage requestMessage, Object result) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setReturnCode(1);
        responseMessage.setErrorMessage("");
        responseMessage.setBody(result.toString()); // 这里需要序列化
        responseMessage.setMessageId(requestMessage.getMessageId());
        return responseMessage;
    }
}
