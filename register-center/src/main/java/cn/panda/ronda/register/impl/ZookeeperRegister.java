package cn.panda.ronda.register.impl;

import cn.panda.ronda.register.api.IRegisterApi;
import cn.panda.ronda.register.domain.TransportConfig;
import cn.panda.ronda.register.domain.TransportInfo;
import cn.panda.ronda.register.infrastructure.ZookeeperComponent;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * todo timeout是否要重新设置？
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

    private String populatePath(TransportConfig transportConfig) {
        return transportConfig.getClassName()
                .concat(SEPARATOR)
                .concat(transportConfig.getProtocol());
    }

    private String populateData(TransportInfo transportInfo) {
        return transportInfo.getHost()
                .concat(SEPARATOR)
                .concat(String.valueOf(transportInfo.getPort()));
    }

    private TransportInfo resolveData(String data) {
        if (data == null || Objects.equals(data, "")) {
            return null;
        }

        String[] split = data.split(SEPARATOR);

        if (split.length < 2) {
            log.error("transportInfo returned is not valid");
            return null;
        }

        String host = split[0];
        Integer port = Integer.parseInt(split[1]);

        TransportInfo transportInfo = new TransportInfo();
        transportInfo.setHost(host);
        transportInfo.setPort(port);

        return transportInfo;
    }
}
