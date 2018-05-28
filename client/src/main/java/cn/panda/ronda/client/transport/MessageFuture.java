package cn.panda.ronda.client.transport;

import cn.panda.ronda.base.remoting.exchange.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * message future
 * created by yongkang.zhang
 * added at 2018/4/10
 */
@Slf4j
public class MessageFuture<V> implements Future<V> {

    private V result;

    private final Channel channel;

    private Long startDate;

    /**
     * 用户设置的超时时间
     */
    private final int timeout;

    /**
     * 是否同步调用，默认是
     */
    private boolean asyncCall;

    public MessageFuture(Channel channel, int timeout) {
        this.channel = channel;
        this.timeout = timeout;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        this.startDate = System.currentTimeMillis();
        V v;
        try {
            v = get(this.timeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("invoke timeout");
            throw new RuntimeException(e);
        }

        return v;
    }

    /**
     * 超时实现
     *
     * @param timeout 超时时间
     * @param unit    单位
     * @return V
     */
    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (this.startDate == null || this.startDate == 0L) {
            this.startDate = System.currentTimeMillis();
        }
        long timeoutSeconds = 0;
        switch (unit) {
            case MICROSECONDS:
                throw new UnsupportedOperationException("Not support this time unit");
            case MILLISECONDS:
                throw new UnsupportedOperationException("Not support this time unit");
            case NANOSECONDS:
                throw new UnsupportedOperationException("Not support this time unit");
            case SECONDS:
                timeoutSeconds = timeout;
                break;
            case MINUTES:
                timeoutSeconds = timeout * 60;
                break;
            case HOURS:
                throw new UnsupportedOperationException("Not support this time unit");
            case DAYS:
                throw new UnsupportedOperationException("Not support this time unit");
            default:
                throw new UnsupportedOperationException("Not support this time unit");
        }
        while (this.result == null) {
            if ((System.currentTimeMillis() - this.startDate) / 1000 >= timeout) {
                throw new TimeoutException();
            } else {
                Thread.sleep(500);
            }
        }
        return this.result;
    }

    public void setResult(V result) {
        this.result = result;
    }
}
