package cn.panda.ronda.spring.annotation;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author yongkang.zhang
 * created at 13/08/2018
 */
public class AnnotationParseApplicationTest {

    @Test
    public void parseTest() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/Ronda.xml");
        DemoConsumer bean = applicationContext.getBean(DemoConsumer.class);
        System.out.println(bean.sayHello());
    }

}
