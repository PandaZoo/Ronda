package cn.panda.ronda.spring.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author yongkang.zhang
 * created at 05/07/2018
 */
public class InvokeInterceptor implements MethodInterceptor {

    /**
     * 在这里实现调用的delegate
     * 如果是consumer端，那么需要取得调用信息封装成RequestMessage， 并且需要ResponseMessage进行反序列化
     * 如果是provider端，那边启动的是netty，consumer连接的就是netty所以可以直接通讯并将返回值序列化为ResponseMessage进行返回
     * @param invocation method invocation
     * @return 返回值
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {


        return null;
    }
}
