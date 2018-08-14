package cn.panda.ronda.spring.advice;

import cn.panda.ronda.base.remoting.exception.ExceptionCode;
import cn.panda.ronda.base.remoting.exception.RondaException;
import cn.panda.ronda.base.remoting.exchange.Channel;
import cn.panda.ronda.client.transport.channel.NettyChannel;
import cn.panda.ronda.client.transport.client.RondaClient;
import cn.panda.ronda.client.transport.config.ChannelConfig;
import cn.panda.ronda.register.domain.TransportConfig;
import cn.panda.ronda.register.domain.TransportInfo;
import cn.panda.ronda.spring.registry.RegistryComponent;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 检查是否连接了provider, 如果没有就进行再连接一次
 * @author yongkang.zhang
 * created at 14/08/2018
 */
@Slf4j
@Component
public class ProviderCheckInterceptor implements MethodInterceptor {

    @Autowired
    private RegistryComponent registryComponent;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        String name = invocation.getMethod().getDeclaringClass().getName();

        Optional<Map.Entry<TransportConfig, Channel>> entry = RondaClient.getCachedMap().entrySet()
                .stream()
                .filter(e -> Objects.equals(e.getKey().getClassName(), name))
                .findFirst();

        if (!entry.isPresent()) {
            throw new RondaException(ExceptionCode.UNKNOWN_ERROR);
        }

        TransportInfo provider = this.registryComponent.getProvider(entry.get().getKey());

        ChannelConfig channelConfig = new ChannelConfig();
        channelConfig.setServiceClass(invocation.getMethod().getDeclaringClass());
        channelConfig.setRemoteIp(provider.getHost());
        channelConfig.setRemotePort(provider.getPort());
        channelConfig.setProtocol(entry.get().getKey().getProtocol());
        NettyChannel nettyChannel = new NettyChannel(channelConfig);
        nettyChannel.connect();
        RondaClient.putCacheMap(entry.get().getKey(), nettyChannel);

        return invocation.proceed();
    }


}
