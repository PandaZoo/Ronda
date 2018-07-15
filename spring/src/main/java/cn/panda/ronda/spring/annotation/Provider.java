package cn.panda.ronda.spring.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 使用实现类上进行注册为provider
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Provider {

    /**
     * bean id
     * @return id
     */
    String id();


    /**
     * 接口类
     * @return Class
     */
    Class<?> interfaceClass();

    /**
     * @return registry
     */
    String registry();

    /**
     * 协议
     */
    String[] protocols();

}
