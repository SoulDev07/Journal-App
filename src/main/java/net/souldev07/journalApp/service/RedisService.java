package net.souldev07.journalApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    public void set(String key, Object o, Long ttl) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key, jsonString, ttl);
        } catch (JsonProcessingException e) {
            log.error("Error occurred while writing to Redis", e);
        }
    }

    public <T> T get(String key, Class<T> entityClass) {
        Object o = redisTemplate.opsForValue().get(key);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(o.toString(), entityClass);
        } catch (Exception e) {
            log.error("Error occurred while reading from Redis", e);
            return null;
        }
    }
}
