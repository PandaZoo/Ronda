package cn.panda.ronda.server.transport.config;

import lombok.Data;

import java.util.List;

/**
 * transport config
 * todo 多种协议如何实现
 * created by yongkang.zhang
 * added at 2018/4/10
 */
@Data
public class TransportConfig {

    private String service;

    private Class<?> clazz;

    private List<String> protocols;


}
