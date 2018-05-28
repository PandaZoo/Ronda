package cn.panda.ronda.client.transport.config;

import lombok.Data;

/**
 * transport config
 * created by yongkang.zhang
 * added at 2018/4/10
 */
@Data
public class TransportConfig {

    private Class<?> serviceClass;

    private String remoteIp;

    private Integer remotePort;

    private String protocol;


}
