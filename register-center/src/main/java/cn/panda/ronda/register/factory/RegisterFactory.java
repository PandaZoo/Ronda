package cn.panda.ronda.register.factory;

import cn.panda.ronda.base.remoting.exception.ExceptionCode;
import cn.panda.ronda.base.remoting.exception.RondaException;
import cn.panda.ronda.register.api.IRegisterApi;
import cn.panda.ronda.register.domain.RondaRegister;
import cn.panda.ronda.register.enums.RegisterTypeEnum;
import cn.panda.ronda.register.impl.RedisRegister;
import cn.panda.ronda.register.impl.ZookeeperRegister;
import cn.panda.ronda.register.infrastructure.RedisComponent;
import cn.panda.ronda.register.infrastructure.ZookeeperComponent;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author yongkang.zhang
 * created at 13/08/2018
 */
@Slf4j
public class RegisterFactory {

    private static final IRegisterApi registerApi = null;

    private static final ZookeeperRegister zookeeperRegister = null;

    public static IRegisterApi getRegisterApi(RondaRegister rondaRegister) {
        RegisterTypeEnum registerTypeEnum = RegisterTypeEnum.fromCode(rondaRegister.getRegisterType());
        if (registerTypeEnum == null) {
            throw new RondaException(ExceptionCode.REGISTER_TYPE_ERROR);
        }

        IRegisterApi iRegisterApi = null;

        switch (registerTypeEnum) {
            case ZOOKEEPER:
                iRegisterApi = zookeeperRegister(rondaRegister);
                break;
            case REDIS:
                iRegisterApi = redisRegister(rondaRegister);
                break;
            default:
                iRegisterApi = null;
                break;
        }

        return iRegisterApi;
    }


    private static ZookeeperRegister zookeeperRegister(RondaRegister rondaRegister) {
        try {
            String host = rondaRegister.getHost();
            ZookeeperComponent zookeeperComponent = new ZookeeperComponent(host);

            zookeeperComponent.connectZookeeper();
            zookeeperComponent.init();
            return new ZookeeperRegister(zookeeperComponent);
        } catch (Exception e) {
            log.error("连接注册中心{}发生错误!", rondaRegister, e);
            throw new RondaException(ExceptionCode.REGISTER_ZOOKEEPER_ERROR);
        }
    }

    private static RedisRegister redisRegister(RondaRegister rondaRegister) {
        try {
            String host = rondaRegister.getHost();
            RedisComponent redisComponent = new RedisComponent(host);

            redisComponent.connect();

            return new RedisRegister(redisComponent);
        } catch (Exception e) {
            log.error("连接注册中心{}发生错误!", rondaRegister, e);
            throw new RondaException(ExceptionCode.REGISTER_REDIS_ERROR);
        }
    }
}
