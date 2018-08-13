package cn.panda.ronda.spring.annotation;

import org.springframework.stereotype.Component;

/**
 * @author yongkang.zhang
 * created at 12/08/2018
 */
@Component
public class DemoConsumer {

    @Consumer(id = "demoProvider", interfaceClass = DemoProvider.class)
    private DemoProvider demoProvider;

    public String sayHello() {
        return demoProvider.sayHello();
    }

}
