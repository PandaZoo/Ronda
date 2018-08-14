package cn.panda.ronda.register.impl;

import cn.panda.ronda.register.api.IRegisterApi;
import cn.panda.ronda.register.domain.TransportConfig;
import cn.panda.ronda.register.domain.TransportInfo;
import cn.panda.ronda.register.infrastructure.RedisComponent;

import static cn.panda.ronda.register.convert.TransportConverter.populateData;
import static cn.panda.ronda.register.convert.TransportConverter.populatePath;
import static cn.panda.ronda.register.convert.TransportConverter.resolveData;

/**
 * @author yongkang.zhang
 * created at 14/08/2018
 */
public class RedisRegister implements IRegisterApi {

    private RedisComponent redisComponent;

    public RedisRegister(RedisComponent redisComponent) {
        this.redisComponent = redisComponent;
    }

    @Override
    public void register(TransportConfig transportConfig, TransportInfo transportInfo) {
        this.redisComponent.setData(populatePath(transportConfig), populateData(transportInfo));
    }

    @Override
    public TransportInfo queryByTransportConfig(TransportConfig config) {
        return resolveData(this.redisComponent.getData(populatePath(config)));
    }
}
