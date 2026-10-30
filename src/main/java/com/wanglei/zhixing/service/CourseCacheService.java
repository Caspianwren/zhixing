package com.wanglei.zhixing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanglei.zhixing.common.BusinessException;
import com.wanglei.zhixing.common.ResultCode;
import com.wanglei.zhixing.vo.CourseDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程详情缓存。
 * <p>
 * 没用 Spring Cache 注解，而是直接用 RedisTemplate 手写，好处是能自己控制：
 * 1. key 的命名和过期时间（正常数据 30 分钟，不存在的 id 只缓存 5 分钟）
 * 2. 空值缓存：查不到也写一个标记，防止同一个不存在的 id 反复打到数据库（缓存穿透）
 * 3. Redis 挂了自动降级：读写异常只打日志，不影响主流程，直接查库
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseCacheService {

    private static final String KEY_PREFIX = "school:course:detail:";
    private static final String NULL_MARK = "__NULL__";
    private static final Duration TTL = Duration.ofMinutes(30);
    private static final Duration NULL_TTL = Duration.ofMinutes(5);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public CourseDetailVO get(Long courseId) {
        String key = key(courseId);
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) {
                return null;
            }
            if (NULL_MARK.equals(json)) {
                // 之前查过这个 id 但不存在，直接拒绝，不用再查库
                throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
            }
            return objectMapper.readValue(json, CourseDetailVO.class);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            // Redis 连不上、序列化失败都走这里：降级成查库
            log.warn("读取课程缓存失败，降级查库：{}", e.getMessage());
            return null;
        }
    }

    public void put(Long courseId, CourseDetailVO detail) {
        try {
            redisTemplate.opsForValue().set(key(courseId), objectMapper.writeValueAsString(detail), TTL);
        } catch (Exception e) {
            log.warn("写入课程缓存失败：{}", e.getMessage());
        }
    }

    public void putNull(Long courseId) {
        try {
            redisTemplate.opsForValue().set(key(courseId), NULL_MARK, NULL_TTL);
        } catch (Exception e) {
            log.warn("写入空值标记失败：{}", e.getMessage());
        }
    }

    /**
     * 课程或章节变更时清掉所有详情缓存。
     * 用 SCAN 而不是 KEYS，因为 KEYS 会阻塞 Redis——数据量大时是事故。
     */
    public void evictAll() {
        try (Cursor<String> cursor = redisTemplate.scan(
                ScanOptions.scanOptions().match(KEY_PREFIX + "*").count(200).build())) {
            List<String> keys = new ArrayList<>();
            cursor.forEachRemaining(keys::add);
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
            log.info("清理课程详情缓存 {} 个", keys.size());
        } catch (Exception e) {
            log.warn("清理课程缓存失败：{}", e.getMessage());
        }
    }

    private String key(Long courseId) {
        return KEY_PREFIX + courseId;
    }
}
