package cn.panda.ronda.spring.config;

/**
 * @author asdsut
 * created at 28/05/2018
 */
public class ServiceConfig<T> implements java.io.Serializable {

    private static final long serialVersionUID = 8765604231518890787L;


    private String interfaceName;
    private Class<?> interfaceClass;
    private T ref;

    public ServiceConfig() {
    }

}
