package cn.panda.ronda.register.enums;

/**
 * @author yongkang.zhang
 * created at 13/08/2018
 */
public enum RegisterTypeEnum {

    ZOOKEEPER("zookeeper", "zookeeper注册中心"), REDIS("redis", "redis注册中心");

    private String code;
    private String desc;

    RegisterTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static RegisterTypeEnum fromCode(String code) {
        if (code == null) {
            return null;
        }

        for (RegisterTypeEnum enumValue : RegisterTypeEnum.values()) {
            if (code.equalsIgnoreCase(enumValue.code)) {
                return enumValue;
            }
        }

        return null;
    }
}
