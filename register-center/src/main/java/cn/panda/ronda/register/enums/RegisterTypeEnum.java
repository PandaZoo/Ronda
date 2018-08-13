package cn.panda.ronda.register.enums;

/**
 * @author yongkang.zhang
 * created at 13/08/2018
 */
public enum RegisterTypeEnum {

    ZOOKEEPER(1, "zookeeper注册中心"), REDIS(2, "redis注册中心");

    private Integer code;
    private String desc;

    RegisterTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static RegisterTypeEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }

        for (RegisterTypeEnum enumValue : RegisterTypeEnum.values()) {
            if (code.equals(enumValue.code)) {
                return enumValue;
            }
        }

        return null;
    }
}
