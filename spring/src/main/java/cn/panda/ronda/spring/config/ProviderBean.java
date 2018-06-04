package cn.panda.ronda.spring.config;

import cn.panda.ronda.base.remoting.codec.CodecTypeEnum;
import cn.panda.ronda.base.remoting.exchange.ServerChannel;
import cn.panda.ronda.server.transport.channel.ServerNettyChannel;
import cn.panda.ronda.server.transport.config.URL;
import cn.panda.ronda.server.transport.server.RondaServer;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;

import java.lang.reflect.Method;
import java.util.List;

/**
 * todo 这里为什么要加service注解
 * @author asdsut
 * created at 28/05/2018
 */
@Data
public class ProviderBean<T> extends ServiceConfig<T> implements InitializingBean, DisposableBean, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent>,
        BeanNameAware {


    private static final long serialVersionUID = 5649428502281367789L;

    private static transient ApplicationContext SPRING_CONTEXT;

    private transient ApplicationContext applicationContext;

    private transient String beanName;

    private transient boolean supportedApplicationListener;

    private List<CodecTypeEnum> protocols;


    public ProviderBean() {
        super();
        // this.service = null;
    }

    public static ApplicationContext getSpringContext() {
        return SPRING_CONTEXT;
    }

    /**
     * InitializingBean
     * 主要是为了export
     */
    public void afterPropertiesSet() throws Exception {
        doExport();
    }

    private void doExport() {
        for (CodecTypeEnum type : this.getProtocols()) {
            URL url = new URL();
            url.setAddress("127.0.0.1");
            url.setPort(22000);
            url.setProtocol(String.valueOf(type.getCode()));
            ServerChannel serverChannel = new ServerNettyChannel(url);
            RondaServer.putChannelMap(url, serverChannel);
        }

    }

    /**
     * DisposableBean
     */
    public void destroy() throws Exception {
        // do nothing
    }

    /**
     * ApplicationContextAware
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        SPRING_CONTEXT = applicationContext;
        try {
            Method method = applicationContext.getClass().getMethod("addApplicationListener", new Class<?>[]{ApplicationListener.class});
            method.invoke(applicationContext, new Object[]{this});
            supportedApplicationListener = true;
        } catch (Throwable t) {
            if (applicationContext instanceof AbstractApplicationContext) {
                try {
                    Method method = AbstractApplicationContext.class.getDeclaredMethod("addListener", new Class<?>[]{ApplicationListener.class});
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    method.invoke(applicationContext, new Object[]{this});
                    supportedApplicationListener = true;
                } catch (Throwable ignored) {

                }
            }
        }
    }

    /**
     * ApplicationListener
     */
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

    }

    /**
     * BeanNameAware
     */
    public void setBeanName(String name) {
        this.beanName = name;
    }
}
