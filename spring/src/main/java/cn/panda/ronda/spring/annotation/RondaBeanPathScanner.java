package cn.panda.ronda.spring.annotation;


import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import java.util.Set;

/**
 * @author yongkang.zhang
 * created at 15/07/2018
 */
public class RondaBeanPathScanner extends ClassPathBeanDefinitionScanner {

   public RondaBeanPathScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment,
                               ResourceLoader resourceLoader) {
       super(registry, useDefaultFilters);

       setEnvironment(environment);

       setResourceLoader(resourceLoader);

       AnnotationConfigUtils.registerAnnotationConfigProcessors(registry);
   }

   public RondaBeanPathScanner(BeanDefinitionRegistry registry, Environment environment,
                               ResourceLoader resourceLoader) {
       this(registry, false, environment, resourceLoader);
   }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        return super.doScan(basePackages);
    }

    public boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
       return super.checkCandidate(beanName, beanDefinition);
    }
}
