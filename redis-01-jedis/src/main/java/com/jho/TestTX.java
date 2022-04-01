package com.jho;

import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

/**
 * @Author JHO
 * @Date 2021-04-23 7:07
 */
public class TestTX {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 6379;
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {
        // 创建Jedis对象
        Jedis jedis = new Jedis(new HostAndPort(HOST, PORT));
        jedis.auth(PASSWORD);

        // 清空数据库
        jedis.flushDB();

        JSONObject user1 = new JSONObject();
        user1.put("name", "jho");
        user1.put("age", 18);

        JSONObject user2 = new JSONObject();
        user2.put("name", "xu-jihong");
        user2.put("age", 8);

        // 开启事务
        Transaction multi = jedis.multi();

        jedis.watch("user1", "user2");

        Response<String> response = null;
        try {
            response = multi.set("user1", user1.toJSONString());
            System.out.println(response);

            int i = 1 / 0;

            response = multi.set("user2", user2.toJSONString());
            System.out.println(response);
            multi.exec();   // 执行事务
        } catch (Exception e) {
            // 放弃事务
            System.out.println("放弃事务");
            multi.discard();
        } finally {
            System.out.println(jedis.get("user1"));
            System.out.println(jedis.get("user2"));
            jedis.close();
        }



    }

}
