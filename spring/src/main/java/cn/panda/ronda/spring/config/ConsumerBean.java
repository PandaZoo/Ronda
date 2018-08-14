package cn.panda.ronda.spring.config;

import cn.panda.ronda.base.remoting.codec.CodecTypeEnum;
import cn.panda.ronda.base.remoting.exception.ExceptionCode;
import cn.panda.ronda.base.remoting.exception.RondaException;
import cn.panda.ronda.client.transport.channel.NettyChannel;
import cn.panda.ronda.client.transport.client.RondaClient;
import cn.panda.ronda.client.transport.config.ChannelConfig;
import cn.panda.ronda.register.domain.TransportConfig;
import cn.panda.ronda.register.domain.TransportInfo;
import cn.panda.ronda.spring.advice.InvokeInterceptor;
import cn.panda.ronda.spring.advice.ProviderCheckInterceptor;
import cn.panda.ronda.spring.annotation.Consumer;
import cn.panda.ronda.spring.annotation.Provider;
import cn.panda.ronda.spring.registry.RegistryComponent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

/**
 * @author asdsut
 * created at 28/05/2018
 */
@Slf4j
@Data
public class ConsumerBean<T> extends AbstractInterfaceConfig implements FactoryBean<T>, java.io.Serializable, InitializingBean, DisposableBean, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent>,
        BeanNameAware {

    private static final long serialVersionUID = 5089964364356016136L;

    private String id;
    private transient String beanName;
    private List<CodecTypeEnum> protocols;
    private Class<?> clazz;
    private T provider;
    private Consumer consumer;
    private RegistryComponent registryComponent;
    private ApplicationContext applicationContext;

    public ConsumerBean(Consumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void destroy() throws Exception {
        log.error("disconnect from server");
    }

    /**
     * do something here
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        connect();
    }

    private void connect() {

        if (protocols == null || protocols.isEmpty()) {
            throw new RondaException(ExceptionCode.CHANNEL_NOT_FOUND);
        }

        this.registryComponent = this.applicationContext.getBean(RegistryComponent.class);

        for (CodecTypeEnum codecTypeEnum : protocols) {

            TransportConfig transportConfig = new TransportConfig();
            transportConfig.setClassName(this.clazz.getName());
            transportConfig.setProtocol(String.valueOf(codecTypeEnum.getCode()));

            TransportInfo transportInfo = this.registryComponent.getProvider(transportConfig);

            if (transportInfo == null) {
                RondaClient.putCacheMap(transportConfig, null);
            } else {
                ChannelConfig channelConfig = new ChannelConfig();
                channelConfig.setServiceClass(this.clazz);
                channelConfig.setRemoteIp(transportInfo.getHost());
                channelConfig.setRemotePort(transportInfo.getPort());
                channelConfig.setProtocol(transportConfig.getProtocol());
                NettyChannel nettyChannel = new NettyChannel(channelConfig);
                nettyChannel.connect();
                RondaClient.putCacheMap(transportConfig, nettyChannel);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 暂时没有lazy加载的bean
     * @param contextRefreshedEvent 刷新事件
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

    }

    /**
     *
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getObject() throws Exception {
        ProxyFactory proxyFactory = new ProxyFactory(this.clazz, applicationContext.getBean(ProviderCheckInterceptor.class));
        proxyFactory.addAdvice(applicationContext.getBean(InvokeInterceptor.class));
        return (T) proxyFactory.getProxy();
    }

    @Override
    public Class<?> getObjectType() {
        return this.clazz;
    }
}
