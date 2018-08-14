package cn.panda.ronda.base.remoting.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * request message
 * created by yongkang.zhang
 * added at 2018/4/9
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class RequestMessage extends BaseMessage {

    private static final long serialVersionUID = 3388020833858391606L;
}
