package cn.panda.ronda.base.remoting.codec;

import cn.panda.ronda.base.remoting.exception.ExceptionCode;
import cn.panda.ronda.base.remoting.exception.RondaException;
import cn.panda.ronda.base.remoting.helper.EnumKeyInterface;

public enum CodecTypeEnum implements EnumKeyInterface {

    MSGPACK(1), JSON(2), HESSIAN(4);

    private Integer code;

    CodecTypeEnum(Integer code) {
        this.code = code;
    }


    @Override
    public Object key() {
        return this.code;
    }

    public static CodecInterface getCodecByType(CodecTypeEnum codecTypeEnum) {
        switch (codecTypeEnum) {
            case JSON:
                return new JsonCodec();
            case HESSIAN:
                return new HessianCodec();
            case MSGPACK:
                return new MsgpackCodec();
            default:
                throw new RondaException(ExceptionCode.PROTOCOL_NOT_FOUND);
        }
    }

    public Integer getCode() {
        return code;
    }
}
