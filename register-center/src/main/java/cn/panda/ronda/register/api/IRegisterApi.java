package cn.panda.ronda.register.api;

import cn.panda.ronda.register.domain.TransportConfig;
import cn.panda.ronda.register.domain.TransportInfo;

/**
 * @author yongkang.zhang
 * created at 13/08/2018
 */
public interface IRegisterApi {

    /**
     * 注册提供者信息
     * @param transportConfig config
     * @param transportInfo 提供者信息
     */
    void register(TransportConfig transportConfig, TransportInfo transportInfo);

    /**
     * 根据config进行查询
     * @param config config
     * @return 连接的必须信息
     */
    TransportInfo queryByTransportConfig(TransportConfig config);
}
