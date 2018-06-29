package cn.panda.ronda.spring.config;

import cn.panda.ronda.base.remoting.codec.CodecTypeEnum;
import cn.panda.ronda.base.remoting.exception.ExceptionCode;
import cn.panda.ronda.base.remoting.exception.RondaException;
import cn.panda.ronda.client.transport.channel.NettyChannel;
import cn.panda.ronda.client.transport.client.RondaClient;
import cn.panda.ronda.client.transport.config.TransportConfig;
import cn.panda.ronda.server.transport.config.URL;
import cn.panda.ronda.server.transport.server.RondaServer;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
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
public class ConsumerBean implements java.io.Serializable, InitializingBean, DisposableBean, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent>,
        BeanNameAware {

    private static final long serialVersionUID = 5089964364356016136L;

    private transient String beanName;
    private List<CodecTypeEnum> protocols;
    private Class<?> clazz;

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

    }

    private void connect() {
        if (protocols == null || protocols.isEmpty()) {
            throw new RondaException(ExceptionCode.CHANNEL_NOT_FOUND);
        }

        for (CodecTypeEnum codecTypeEnum : protocols) {
            URL url = RondaServer.getChannelMap(codecTypeEnum);
            assert url != null;

            TransportConfig transportConfig = new cn.panda.ronda.client.transport.config.TransportConfig();
            transportConfig.setServiceClass(clazz);
            transportConfig.setRemoteIp(url.getAddress());
            transportConfig.setRemotePort(url.getPort());
            transportConfig.setProtocol(String.valueOf(codecTypeEnum.getCode()));
            NettyChannel nettyChannel = new NettyChannel(transportConfig);
            RondaClient.putRemoteMap(transportConfig, nettyChannel);
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

    }

}
