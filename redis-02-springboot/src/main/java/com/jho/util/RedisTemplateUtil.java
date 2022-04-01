package com.jho.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author JHO
 * @Date 2021-05-18 20:45
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RedisTemplateUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 指定key过期时间
     * @param key key
     * @param timeout 时间（秒）
     * @return {@link boolean}
     */
    public boolean expire(String key, long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 指定key过期时间，指定单位时间
     * @param key key
     * @param timeout 时间
     * @param timeUnit 时间单位
     * @return {@link boolean}
     */
    public boolean expire(String key, long timeout, TimeUnit timeUnit) {
        Boolean ret = redisTemplate.expire(key, timeout, timeUnit);
        return ret != null && ret;
    }

    /**
     * 获取key的过期时间
     *
     * @param key key
     * @return {@link Long} 时间（秒），返回0代表永久有效
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     * @param key key
     * @return {@link Boolean} true：存在 false：不存在
     */
    public Boolean hasKey(String key) {
        try {
            return this.redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除一个或者多个key
     * @param key key
     * @return {@link Long} 删除key的数量
     */
    public Long del(String... key) {
        if (key == null || key.length <= 0) return 0L;
        if (key.length == 1) {
            Boolean ret = this.redisTemplate.delete(key[0]);
            return ret != null && ret ? 1L : 0L;
        }
        return this.redisTemplate.delete(new ArrayList<>(Arrays.asList(key)));
    }

    /**
     * 普通缓存获取
     *
     * @param key key
     * @return {@link Object}
     */
    public Object get(String key) {
        return key == null ? null : this.redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存存放
     *
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            this.redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存存放并设置过期时间
     *
     * @param key 键
     * @param value 值
     * @param time 时间（秒） time大于0 如果time小于等于0，则缓存不限过期时间
     * @return true成功 false失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                this.redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                return set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key 键
     * @param delta 要增加的数量（大于0）
     * @return
     */
    public Long increment(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return this.redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key 键
     * @param delta 要减少的数量（大于0）
     * @return
     */
    public Long decrement(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return this.redisTemplate.opsForValue().decrement(key, delta);
    }

    // ====================Map====================

    /**
     * Hash get
     *
     * @param key 键
     * @param item 项
     * @return {@link Object}
     */
    public Object hget(String key, String item) {
        return this.redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 向hash表中存放数据，如果不存在则创建
     *
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true 成功 false 失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            this.redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向hash表存放数据，如果不存在则创建
     *
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time 时间（秒） 注意：如果已存在hash表有过期时间，这里将会替换原有的时间
     * @return true 成功 false 失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            boolean isSuccess = hset(key, item, value);
            if (time > 0) {
                isSuccess = expire(key, time);
            }
            return isSuccess;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取某个key对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return this.redisTemplate.opsForHash().entries(key);
    }


    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            this.redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key 键
     * @param item 项
     * @return {@link Long}
     */
    public Long hdel(String key, Object... item) {
        return this.redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key 键
     * @param item 项
     * @return true存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return this.redisTemplate.opsForHash().hasKey(key, item);
    }



}
