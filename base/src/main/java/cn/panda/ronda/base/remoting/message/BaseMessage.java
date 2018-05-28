package cn.panda.ronda.base.remoting.message;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * base message for transfer
 * created by yongkang.zhang
 * added at 2018/4/9
 */
@Data
public class BaseMessage implements Serializable {

    /**
     * identifier id
     */
    private Long messageId;

    private String targetClass;

    private String targetMethod;

    private Class[] argType;

    // 为什么需要argClass
    // 如果这里要用msgpack那么就不能使用Object类型，如果转换成String类型，那么就是需要先序列化成string，然后再用msgpack序列化
    private Object[] args;

    /**
     * message body
     */
    private String body;

    private Map<String, Object> attributesMap;

}
