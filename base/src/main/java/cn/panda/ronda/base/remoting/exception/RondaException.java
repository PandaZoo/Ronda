package cn.panda.ronda.base.remoting.exception;

public class RondaException extends BaseException {

    public RondaException(ExceptionCode errorCode, Object... objs) {
        super(errorCode, objs);
    }

    public RondaException(ExceptionCode errorCode, Throwable throwable, Object... objs) {
        super(errorCode, throwable, objs);
    }
}
