package cn.panda.ronda.spring.advice;

import cn.panda.ronda.base.remoting.message.RequestMessage;
import cn.panda.ronda.base.remoting.message.ResponseMessage;
import cn.panda.ronda.client.transport.client.RondaClient;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

/**
 * @author yongkang.zhang
 * created at 05/07/2018
 */
@Slf4j
public class InvokeInterceptor implements MethodInterceptor {

    private RondaClient rondaClient = new RondaClient();

    /**
     * 在这里实现调用的delegate
     * 如果是consumer端，那么需要取得调用信息封装成RequestMessage， 并且需要ResponseMessage进行反序列化
     * 如果是provider端，那边启动的是netty，consumer连接的就是netty所以可以直接通讯并将返回值序列化为ResponseMessage进行返回
     * @param invocation method invocation
     * @return 返回值
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("InvokeInterceptor intercepting..., invocation: {}", invocation);
        RequestMessage requestMessage = invocationToRequestMessage(invocation);
        ResponseMessage responseMessage = rondaClient.invoke(requestMessage);

        // 判断response message
        if (responseMessage.getReturnCode() == 1) {
            String body = responseMessage.getBody();
            // deserialize
            log.info("返回结果是: {}", body);
        } else {
            log.error("请求错误, invocation: {}, requestMessage: {}, responseMessage: {}", invocation, requestMessage, responseMessage);
        }

        return null;
    }

    public RequestMessage invocationToRequestMessage(MethodInvocation invocation) {
        RequestMessage requestMessage = new RequestMessage();
        Method method = invocation.getMethod();
        requestMessage.setTargetClass(method.getDeclaringClass().getName());
        requestMessage.setTargetMethod(method.getName());
        requestMessage.setArgType(method.getParameterTypes());
        requestMessage.setArgs(invocation.getArguments());
        // todo 是否替换更更唯一的
        requestMessage.setMessageId(System.currentTimeMillis());
        return requestMessage;
    }
}
