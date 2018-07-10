package cn.panda.ronda.spring.schema;

import cn.panda.ronda.base.remoting.codec.CodecTypeEnum;
import cn.panda.ronda.base.remoting.exception.ExceptionCode;
import cn.panda.ronda.base.remoting.exception.RondaException;
import cn.panda.ronda.spring.config.ConsumerBean;
import cn.panda.ronda.spring.config.ProviderBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author asdsut
 * created at 28/05/2018
 */
@Slf4j
public class RondaBeanDefinitionParser implements BeanDefinitionParser {

    private final Class<?> beanClass;
    private final boolean required;

    public RondaBeanDefinitionParser(Class<?> beanClass, boolean required) {
        this.beanClass = beanClass;
        this.required = required;
    }

    private static BeanDefinition parse(Element element, ParserContext parserContext, Class<?> beanClass, boolean required) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setLazyInit(false);
        String id = element.getAttribute("id");
        if ((id == null || id.length() == 0) && required) {
            String generatedBeanName = element.getAttribute("name");
            if (generatedBeanName == null || generatedBeanName.length() == 0) {
                generatedBeanName = element.getAttribute("interface");
            }
            if (generatedBeanName == null || generatedBeanName.length() == 0) {
                generatedBeanName = beanClass.getName();
            }
            id = generatedBeanName;
            int counter = 2;
            while (parserContext.getRegistry().containsBeanDefinition(id)) {
                id = generatedBeanName + (counter++);
            }
        }
        if (id != null && id.length() > 0) {
            if (parserContext.getRegistry().containsBeanDefinition(id)) {
                throw new IllegalStateException("Duplicated spring bean id " + id);
            }
            parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
            beanDefinition.getPropertyValues().add("id", id);
        }

        if (ProviderBean.class.isAssignableFrom(beanClass)) {
            parseProviderBean(beanDefinition, element, parserContext);
        }

        if (ConsumerBean.class.isAssignableFrom(beanClass)) {
            parseConsumerBean(beanDefinition, element, parserContext);
        }

        return beanDefinition;
    }

    @SuppressWarnings("Duplicates")
    private static void parseProviderBean(RootBeanDefinition rootBeanDefinition, Element element, ParserContext parserContext) {
        List<CodecTypeEnum> codecTypeEnumList = new ArrayList<>();
        String protocol = element.getAttribute("protocol");
        if (protocol == null || Objects.equals(protocol, "")) {
            codecTypeEnumList.add(CodecTypeEnum.HESSIAN);
        } else {
            String[] protocols = protocol.split(",");
            for (String item : protocols) {
                Optional<CodecTypeEnum> optional = CodecTypeEnum.getByValue(String.valueOf(item).toLowerCase());
                if (optional.isPresent()) {
                    codecTypeEnumList.add(optional.get());
                } else {
                    throw new RondaException(ExceptionCode.PROTOCOL_NOT_FOUND);
                }
            }
        }
        rootBeanDefinition.getPropertyValues().add("protocols", codecTypeEnumList);

        // parse interface
        String interfaceName = element.getAttribute("interface");
        try {
            Class<?> aClass = Class.forName(interfaceName);
            rootBeanDefinition.getPropertyValues().add("interfaceClass", aClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // parse ref
        String refBeanName = element.getAttribute("ref");
        if (parserContext.getRegistry().containsBeanDefinition(refBeanName)) {
            rootBeanDefinition.getPropertyValues().add("ref", new RuntimeBeanNameReference(refBeanName));
        }

    }

    @SuppressWarnings("Duplicates")
    private static void parseConsumerBean(RootBeanDefinition rootBeanDefinition, Element element, ParserContext parserContext) {
        List<CodecTypeEnum> codecTypeEnumList = new ArrayList<>();
        String protocol = element.getAttribute("protocol");
        if (protocol == null || Objects.equals(protocol, "")) {
            codecTypeEnumList.add(CodecTypeEnum.HESSIAN);
        } else {
            String[] protocols = protocol.split(",");
            for (String item : protocols) {
                Optional<CodecTypeEnum> optional = CodecTypeEnum.getByValue(String.valueOf(item).toLowerCase());
                if (optional.isPresent()) {
                    codecTypeEnumList.add(optional.get());
                } else {
                    throw new RondaException(ExceptionCode.PROTOCOL_NOT_FOUND);
                }
            }
        }
        rootBeanDefinition.getPropertyValues().add("protocols", codecTypeEnumList);

        // parse interface
        String interfaceName = element.getAttribute("interface");
        try {
            Class<?> aClass = Class.forName(interfaceName);
            rootBeanDefinition.getPropertyValues().add("clazz", aClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return parse(element, parserContext, beanClass, required);
    }
}
