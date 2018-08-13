package cn.panda.ronda.base.remoting.exchange;

import cn.panda.ronda.base.remoting.message.RequestMessage;
import cn.panda.ronda.base.remoting.message.ResponseMessage;

/**
 * exchange client
 * created by yongkang.zhang
 * added at 2018/4/9
 */
public interface ExchangeClient extends Channel {

    ResponseMessage invoke(RequestMessage requestMessage);

    default Boolean isConnected() {
        return null;
    }
}
