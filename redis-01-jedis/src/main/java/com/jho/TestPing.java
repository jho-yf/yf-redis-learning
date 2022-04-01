package com.jho;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

/**
 * @Author JHO
 * @Date 2021-04-22 23:02
 */
public class TestPing {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 6379;
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {
        // 创建Jedis对象
        Jedis jedis = new Jedis(new HostAndPort(HOST, PORT));
        jedis.auth(PASSWORD);

        String pong = jedis.ping();
        System.out.println(pong);

        // 关闭连接
        jedis.close();
    }

}
