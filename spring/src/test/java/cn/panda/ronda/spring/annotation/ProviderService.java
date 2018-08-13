package cn.panda.ronda.spring.annotation;

import org.springframework.stereotype.Component;

/**
 * @author yongkang.zhang
 * created at 12/08/2018
 */
@Provider(id = "providerService", interfaceClass = DemoProvider.class)
@Component
public class ProviderService implements DemoProvider{

    @Override
    public String sayHello() {
        return "HelloWorld";
    }
}
