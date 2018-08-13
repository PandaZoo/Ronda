package cn.panda.ronda.register.domain;

import lombok.Data;

/**
 * @author yongkang.zhang
 * created at 13/08/2018
 */
@Data
public class TransportConfig {

    // remote class name
    private String className;

    // remote protocol
    private String protocol;
}
