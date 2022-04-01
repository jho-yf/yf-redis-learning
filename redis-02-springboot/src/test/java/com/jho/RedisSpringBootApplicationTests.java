package com.jho;

import com.jho.pojo.User;
import com.jho.util.RedisTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.Resource;

/**
 * @Author JHO
 * @Date 2021-04-28 22:59
 */
@SpringBootTest
@Slf4j
public class RedisSpringBootApplicationTests {

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisTemplateUtil redisTemplateUtil;

    @Test
    void testRedisTemplate() {
        redisTemplate.opsForValue();    // 操作字符串
        redisTemplate.opsForList();     // 操作List
        redisTemplate.opsForSet();      // 操作Set
        redisTemplate.opsForHash();     // 操作Hash
        redisTemplate.opsForZSet();     // 操作zset

        // 获取Redis的连接对象
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        //connection.flushAll();
        connection.flushDb();


        String key = "name";
        redisTemplate.opsForValue().set(key, "许集泓");
        log.info("value={}", redisTemplate.opsForValue().get(key));
    }

    @Test
    public void testSetObject() {
        RedisSerializer keySerializer = redisTemplate.getKeySerializer();
        log.info("keySerializer={}", keySerializer);

        User jho = new User("jho", 18);
        redisTemplate.opsForValue().set("user:jho", jho);
        log.info("user:jho={}", redisTemplate.opsForValue().get("user:jho"));
    }

    @Test
    public void testRedisTemplateUtil() {
        boolean isSet = this.redisTemplateUtil.set("jho", "1030");
        log.info("isSet={}", isSet);
        this.redisTemplateUtil.expire("jho", 10);
        String value = (String) this.redisTemplateUtil.get("jho");
        log.info("value={}", value);
    }


}
