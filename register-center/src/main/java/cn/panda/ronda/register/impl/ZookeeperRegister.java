package cn.panda.ronda.register.impl;

import cn.panda.ronda.register.api.IRegisterApi;
import cn.panda.ronda.register.domain.TransportConfig;
import cn.panda.ronda.register.domain.TransportInfo;
import cn.panda.ronda.register.infrastructure.ZookeeperComponent;
import lombok.extern.slf4j.Slf4j;

import static cn.panda.ronda.register.convert.TransportConverter.*;

/**
 *
 * @author yongkang.zhang
 * created at 13/08/2018
 */
@Slf4j
public class ZookeeperRegister implements IRegisterApi {

    private ZookeeperComponent zookeeperComponent;

    private static final String SEPARATOR = "-";

    public ZookeeperRegister(ZookeeperComponent zookeeperComponent) {
        this.zookeeperComponent = zookeeperComponent;
    }

    public void register(TransportConfig transportConfig, TransportInfo transportInfo) {
        try {
            this.zookeeperComponent.setData(populatePath(transportConfig),
                    populateData(transportInfo));
        } catch (Exception e) {
            log.error("register failed, transportConfig: {}, transportInfo: {}", transportInfo, transportInfo, e);
        }
    }

    public TransportInfo queryByTransportConfig(TransportConfig config) {
        try {
            String data = this.zookeeperComponent.getData(populatePath(config));
            return resolveData(data);
        } catch (Exception e) {
            log.error("query register information failed, config: {}", config, e);
        }
        return null;
    }
}
