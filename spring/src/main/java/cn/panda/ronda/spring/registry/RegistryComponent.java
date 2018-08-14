package cn.panda.ronda.spring.registry;

import cn.panda.ronda.base.remoting.codec.CodecTypeEnum;
import cn.panda.ronda.register.api.IRegisterApi;
import cn.panda.ronda.register.domain.RondaRegister;
import cn.panda.ronda.register.domain.TransportConfig;
import cn.panda.ronda.register.domain.TransportInfo;
import cn.panda.ronda.register.enums.RegisterTypeEnum;
import cn.panda.ronda.register.factory.RegisterFactory;
import cn.panda.ronda.spring.config.ProviderBean;
import cn.panda.ronda.spring.config.RegisterBean;
import cn.panda.ronda.spring.util.HostIdentifierHelper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yongkang.zhang
 * created at 14/08/2018
 */
@Component
public class RegistryComponent implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private IRegisterApi registerApi;

    public void registry(ProviderBean providerBean) {
        Map<TransportConfig, TransportInfo> transportConfigTransportInfoMap = fromProviderBean(providerBean);

        checkRegisterApi();

        transportConfigTransportInfoMap.forEach((key, value) -> this.registerApi.register(key, value));
    }

    public TransportInfo getProvider(TransportConfig transportConfig) {
        checkRegisterApi();

        return this.registerApi.queryByTransportConfig(transportConfig);
    }

    private void checkRegisterApi() {
        if (registerApi != null) {
            return;
        }

        synchronized (RegistryComponent.class) {
            if (registerApi != null) {
                return;
            }

            RegisterBean registerBean = null;
            try {
               registerBean = applicationContext.getBean(RegisterBean.class);
            } catch (Exception ignored) {}

            RondaRegister rondaRegister = new RondaRegister();
            if (registerBean != null) {
                rondaRegister.setHost(registerBean.getAddress().concat(":").concat(registerBean.getPort()));
                rondaRegister.setRegisterType(registerBean.getType());
            } else {
                rondaRegister.setHost("127.0.0.1:6379");
                rondaRegister.setRegisterType(RegisterTypeEnum.REDIS.getCode());
            }

            registerApi = RegisterFactory.getRegisterApi(rondaRegister);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<TransportConfig, TransportInfo> fromProviderBean(ProviderBean providerBean) {
        Map<TransportConfig, TransportInfo> transportMap = new HashMap<>();

        for (CodecTypeEnum codecTypeEnum : (List<CodecTypeEnum>) providerBean.getProtocols()) {
            TransportConfig transportConfig = new TransportConfig();
            transportConfig.setProtocol(String.valueOf(codecTypeEnum.getCode()));
            transportConfig.setClassName(providerBean.getInterfaceClass().getName());

            TransportInfo transportInfo = new TransportInfo();
            transportInfo.setHost(HostIdentifierHelper.getIpAddress());
            // 暂时只用netty
            transportInfo.setPort(22000);

            transportMap.put(transportConfig, transportInfo);
        }

        return transportMap;
    }





    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
