package cn.panda.ronda.base.remoting.exception;

public class BaseException extends RuntimeException {

    private Integer code;
    private String msg;

    public BaseException(ExceptionCode errorCode, Object... objs) {
        this(errorCode, (Throwable) null, objs);
    }

    public BaseException(ExceptionCode errorCode, Throwable throwable, Object... objs) {
        super(objs != null && objs.length > 0 ? String.format(errorCode.getMessage(), objs) : (errorCode != null ? errorCode.getMessage().replace("%s", "") : ""), throwable);
        if (errorCode != null) {
            this.code = errorCode.getCode();

            if (objs == null || objs.length == 0) {
                this.msg = errorCode.getMessage().replace("%s", "");
            } else {
                this.msg = String.format(errorCode.getMessage(), objs);
            }
        }
    }

}
