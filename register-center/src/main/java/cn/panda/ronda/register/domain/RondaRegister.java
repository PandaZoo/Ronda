package cn.panda.ronda.register.domain;

import lombok.Data;

/**
 * @author yongkang.zhang
 * created at 13/08/2018
 */
@Data
public class RondaRegister {

    /**
     * 包括端口, 如 127.0.0.1
     */
    private String host;

    private String registerType;

}
