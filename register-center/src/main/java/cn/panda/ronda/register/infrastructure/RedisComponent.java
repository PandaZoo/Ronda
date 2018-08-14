package cn.panda.ronda.register.infrastructure;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author yongkang.zhang
 * created at 14/08/2018
 */
public class RedisComponent {

    private JedisPool jedisPool;

    private String host;

    private static String RONDA_KEY = "ronda_";

    public RedisComponent(String host) {
        this.host = host;
    }

    public void connect() {
        if (jedisPool == null) {
            String[] hostAndPort = this.host.split(":");
            String pureHost = hostAndPort[0];
            int port = Integer.parseInt(hostAndPort[1]);
            jedisPool = new JedisPool(new JedisPoolConfig(), pureHost, port);
        }
    }

    public void setData(String key, String value) {
        this.jedisPool.getResource().set(RONDA_KEY.concat(key), value);
    }

    public String getData(String key) {
        return this.jedisPool.getResource().get(RONDA_KEY.concat(key));
    }

}
