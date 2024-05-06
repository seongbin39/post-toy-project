package store.sbin.postservice.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 데이터 저장
     * @param key
     * @param value
     */
    public void setData(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 데이터 조회
     * @param key
     * @return
     */
    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 데이터 삭제
     * @param key
     */
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 데이터 저장 및 만료시간 설정
     * @param key
     * @param value
     * @param timeout
     */
    public void setDataExpire(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(timeout));
    }

}
