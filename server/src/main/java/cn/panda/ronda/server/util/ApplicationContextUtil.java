package cn.panda.ronda.server.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 反射帮助类
 */
@Component
public class ApplicationContextUtil {

    private static ApplicationContext context;

    public static <T> T findByClassName(String className) {
        return (T) context.getBean(className);
    }

    @Autowired
    public static void setContext(ApplicationContext context) {
        ApplicationContextUtil.context = context;
    }
}
