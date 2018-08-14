package cn.panda.ronda.register;

import cn.panda.ronda.register.api.IRegisterApi;
import cn.panda.ronda.register.domain.RondaRegister;
import cn.panda.ronda.register.domain.TransportConfig;
import cn.panda.ronda.register.domain.TransportInfo;
import cn.panda.ronda.register.enums.RegisterTypeEnum;
import cn.panda.ronda.register.factory.RegisterFactory;

/**
 * @author yongkang.zhang
 * created at 13/08/2018
 */
public class RegisterFactoryTest {


    @org.junit.Test
    public void test() {
        RondaRegister rondaRegister = new RondaRegister();
        rondaRegister.setHost("127.0.0.1:6379");
        rondaRegister.setRegisterType(RegisterTypeEnum.REDIS.getCode());

        IRegisterApi registerApi = RegisterFactory.getRegisterApi(rondaRegister);

        register(registerApi);

        TransportInfo transportInfo = registerApi.queryByTransportConfig(populateConfig());

        System.out.println("查询到的transportInfo是: " + transportInfo);
    }

    private void register(IRegisterApi registerApi) {
        TransportConfig transportConfig = populateConfig();

        TransportInfo transportInfo = new TransportInfo();
        transportInfo.setHost("127.0.0.1");
        transportInfo.setPort(22000);

        registerApi.register(transportConfig, transportInfo);
    }

    private TransportConfig populateConfig() {
        TransportConfig transportConfig = new TransportConfig();
        transportConfig.setClassName("fullClassName");
        transportConfig.setProtocol("json");

        return transportConfig;
    }
}
