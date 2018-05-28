package cn.panda.ronda.base.remoting.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.msgpack.annotation.Message;

/**
 * response message
 * created by yongkang.zhang
 * added at 2018/4/9
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@Message
public class ResponseMessage extends BaseMessage {

    /**
     * 1 success
     * 0 error
     */
    private Integer returnCode;

    private String errorMessage;

}
