package cn.panda.ronda.spring.schema;

import cn.panda.ronda.spring.annotation.ConsumerAnnotationBeanPostProcessor;
import cn.panda.ronda.spring.annotation.ProviderAnnotationBeanPostProcessor;
import cn.panda.ronda.spring.util.BeanRegistrar;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import static org.springframework.util.StringUtils.commaDelimitedListToStringArray;
import static org.springframework.util.StringUtils.trimArrayElements;

/**
 *
 * @author yongkang.zhang
 * created at 15/07/2018
 */
public class AnnotationBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    /**
     * 通过
     * @param element 元素
     * @param parserContext 解析上下文
     * @param builder builder
     */
    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String packageToScan = element.getAttribute("package");

        String[] packagesToScan = trimArrayElements(commaDelimitedListToStringArray(packageToScan));

        builder.addConstructorArgValue(packagesToScan);

        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

        registerConsumerAnnotationBeanPostProcessor(parserContext.getRegistry());
    }

    @Override
    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }

    private void registerConsumerAnnotationBeanPostProcessor(BeanDefinitionRegistry registry) {
        // Register @Consumer Annotation Bean Processor
        BeanRegistrar.registerInfrastructureBean(registry, ConsumerAnnotationBeanPostProcessor.BEAN_NAME, ConsumerAnnotationBeanPostProcessor.class);
    }

    protected Class<?> getBeanClass(Element element) {
        return ProviderAnnotationBeanPostProcessor.class;
    }
}
