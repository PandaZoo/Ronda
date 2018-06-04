package cn.panda.ronda.spring.schema;

import cn.panda.ronda.base.remoting.codec.CodecTypeEnum;
import cn.panda.ronda.base.remoting.exception.ExceptionCode;
import cn.panda.ronda.base.remoting.exception.RondaException;
import cn.panda.ronda.base.remoting.helper.EnumFunction;
import cn.panda.ronda.spring.config.ProviderBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
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
public class RondaBeanParser implements BeanDefinitionParser {

    private final Class<?> beanClass;
    private final boolean required;

    public RondaBeanParser(Class<?> beanClass, boolean required) {
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



        return null;
    }


    private static void parseProviderBean(RootBeanDefinition rootBeanDefinition, Element element, ParserContext parserContext) {
        List<CodecTypeEnum> codecTypeEnumList = new ArrayList<>();
        String protocol = element.getAttribute("protocol");
        if (protocol == null || Objects.equals(protocol, "")) {
            codecTypeEnumList.add(CodecTypeEnum.HESSIAN);
        } else {
            String[] protocols = protocol.split(",");
            for (String item : protocols) {
                Optional<CodecTypeEnum> optional = EnumFunction.findByKey(CodecTypeEnum.class, String.valueOf(item));
                if (optional.isPresent()) {
                    codecTypeEnumList.add(optional.get());
                } else {
                    throw new RondaException(ExceptionCode.PROTOCOL_NOT_FOUND);
                }
            }
        }
        rootBeanDefinition.getPropertyValues().add("protocols", codecTypeEnumList);

    }


    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return parse(element, parserContext, beanClass, required);
    }
}
