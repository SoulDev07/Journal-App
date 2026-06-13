package net.souldev07.journalApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void set(String key, Object o, Long ttl) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key, jsonString, ttl, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            log.error("Error occurred while writing to Redis", e);
        }
    }

    public <T> T get(String key, Class<T> entityClass) {
        String o = redisTemplate.opsForValue().get(key);
        if (o == null)
            return null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(o, entityClass);
        } catch (Exception e) {
            log.error("Error occurred while reading from Redis", e);
            return null;
        }
    }
}
