package cn.panda.ronda.base.remoting.codec;

import cn.panda.ronda.base.remoting.exception.ExceptionCode;
import cn.panda.ronda.base.remoting.exception.RondaException;
import cn.panda.ronda.base.remoting.helper.EnumKeyInterface;

import java.util.Optional;

/**
 * 需要支持一个key，比如ronda,msgpack用来匹配配置的协议
 */
public enum CodecTypeEnum implements EnumKeyInterface {

    MSGPACK(1, "msgpack"), JSON(2, "json"), HESSIAN(4, "hessian");

    private Integer code;
    private String value;

    CodecTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
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

    public static Optional<CodecTypeEnum> getByValue(String value) {
        if (value == null || value.equals("")) {
            return Optional.empty();
        }

        for (CodecTypeEnum typeEnum : CodecTypeEnum.class.getEnumConstants()) {
            if (typeEnum.getValue().equals(value)) {
                return Optional.of(typeEnum);
            }
        }

        return Optional.empty();
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }
}
