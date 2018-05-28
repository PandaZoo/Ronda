package cn.panda.ronda.base.remoting.exception;

public enum ExceptionCode {

    PROTOCOL_NOT_FOUND(10001, "不支持的序列化协议"),
    CHANNEL_NOT_FOUND(10002, "未找到对应的channel");


    private Integer code;
    private String  message;

    ExceptionCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
