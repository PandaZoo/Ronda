package cn.panda.ronda.server.transport.config;

import lombok.Data;

/**
 * 根据URL进行识别某个Channel
 */
@Data
public class URL {

    /**
     * 地址
     */
    private String address;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 协议
     */
    private String protocol;


}
