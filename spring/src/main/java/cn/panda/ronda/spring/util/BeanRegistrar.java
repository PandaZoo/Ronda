package cn.panda.ronda.spring.util;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * @author yongkang.zhang
 * created at 15/07/2018
 */
public class BeanRegistrar {

    /**
     * Register Infrastructure bean
     *
     */
    public static void registerInfrastructureBean(BeanDefinitionRegistry beanDefinitionRegistry,
                                                 String beanName,
                                                 Class<?> beanType) {
        if (!beanDefinitionRegistry.containsBeanDefinition(beanName)) {
            RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(beanType);
            rootBeanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            beanDefinitionRegistry.registerBeanDefinition(beanName, rootBeanDefinition);
        }
    }
}
