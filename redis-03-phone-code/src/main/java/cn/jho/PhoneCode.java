package cn.jho;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * 手机验证码功能
 *
 * @author JHO xu-jihong@qq.com
 * @date 2021-06-25 23:43
 */
@Slf4j
public class PhoneCode {

    /** 验证码位数 */
    public static final int CODE_LEN = 6;

    /** 限制发送验证码次数 */
    public static final int LIMIT = 3;

    /** redis主机 */
    public static final String REDIS_HOST = "localhost";

    /** redis端口 */
    public static final int REDIS_PORT = 6379;

    /** redis密码 */
    public static final String REDIS_PASSWORD = "123456";

    /** 发送验证码数量key */
    public static final String COUNT_KEY_FORMAT = "verify-code:%s:count";

    /** 验证码key */
    public static final String CODE_KEY_FORMAT = "verify-code:%s:code";

    public static void main(String[] args) throws Exception {
        // 发送验证码
        sendVerifyCode("16620478627");

        // 校验验证码
        // verifyCode("16620478627", "614743");
    }

    /**
     * 随机生成CODE_LEN位验证码
     *
     * @return 验证码 {@link String}
     */
    public static String getCode() throws NoSuchAlgorithmException {
        Random random = SecureRandom.getInstanceStrong();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < CODE_LEN; i++) {
            sb.append(random.nextInt(9));
        }

        return sb.toString();
    }

    /**
     * 每个手机号码每天只能发送3次验证码，过期时间60s
     *
     * @param phone 手机号码
     */
    public static void sendVerifyCode(String phone) {
        Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT);
        jedis.auth(REDIS_PASSWORD);
        try {
            String countKey = String.format(COUNT_KEY_FORMAT, phone);
            String codeKey = String.format(CODE_KEY_FORMAT, phone);
            String count = jedis.get(countKey);
            if (count == null) {
                // 一天过期
                jedis.setex(countKey, 24 * 60 * 60L, "1");
            } else if (Integer.parseInt(count) < LIMIT){
                jedis.incr(countKey);
            } else {
                log.info("一天只能发送{}次验证码", LIMIT);
                jedis.close();
                return;
            }
            String code = getCode();
            log.info("收到验证码：{}", code);
            jedis.setex(codeKey, 60L, code);
        } catch (Exception e) {
            log.info("exception={}", e.toString());
        } finally {
            jedis.close();
        }
    }

    /**
     * 对验证码进行校验
     *
     * @param phone 手机号
     * @param code 待校验的验证码
     */
    public static void verifyCode(String phone, String code) {
        String codeKey = String.format(CODE_KEY_FORMAT, phone);
        Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT);
        jedis.auth(REDIS_PASSWORD);
        String sourceCode = jedis.get(codeKey);
        if (sourceCode != null && sourceCode.equals(code)) {
            log.info("验证码校验成功！");
        } else {
            log.info("验证码校验失败！");
        }
        jedis.close();
    }

}
