package cn.panda.ronda.spring.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 使用在引用上，解析为consumer
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Consumer {

    String id();

    Class<?> interfaceClass();

    String interfaceName() default "";

    String registry() default "";

    String[] protocols() default "json";

    int retryTimes() default 1;

    long timeout() default 5000;
}
