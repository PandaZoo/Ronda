package cn.panda.ronda.spring.parse;

import cn.panda.ronda.spring.service.HelloService;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

/**
 * @author yongkang.zhang
 * created at 06/07/2018
 */
public class XmlParseApplicationTest {

    @Test
    public void parseTest() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/Ronda.xml");
        int beanDefinitionCount = applicationContext.getBeanDefinitionCount();
        HelloService helloService = applicationContext.getBean("firstProvider", HelloService.class);
        Assert.isTrue(helloService != null, "parse error");
        System.out.println(helloService.sayHello());
    }
}
