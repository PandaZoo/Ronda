package cn.panda.ronda.spring.schema;

import cn.panda.ronda.spring.config.ConsumerBean;
import cn.panda.ronda.spring.config.ProviderBean;
import cn.panda.ronda.spring.config.RegisterBean;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author asdsut
 * created at 28/05/2018
 */
public class RondaNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {
        registerBeanDefinitionParser("provider", new RondaBeanDefinitionParser(ProviderBean.class, true));
        registerBeanDefinitionParser("consumer", new RondaBeanDefinitionParser(ConsumerBean.class, true));
        registerBeanDefinitionParser("registry", new RondaBeanDefinitionParser(RegisterBean.class, true));
    }
}
