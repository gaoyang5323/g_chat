package com.kakuiwong.service.impl;

import com.kakuiwong.constant.XConstant;
import com.kakuiwong.model.po.XUser;
import com.kakuiwong.service.UserChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
@Service
public class UserChannelServiceimpl implements UserChannelService {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public String getHostByUserId(String recipientUserId) {
        return (String) getValueByKey(recipientUserId);
    }

    @Override
    public boolean online(String userId, String nettyUrl) {
        setValue(userId, nettyUrl);
        incrementOnlineCount(covertToCountHost(nettyUrl));
        return true;
    }

    @Override
    public boolean offLine(String userId, String nettyUrl) {
        deleteByKey(userId);
        decrementOnlineCount(covertToCountHost(nettyUrl));
        return true;
    }

    @Override
    public XUser getUserByToken(String token) {
        return (XUser) getValueByKey(token);
    }

    @Override
    public boolean login(String token, XUser user) {
        setValue(token, user, Duration.ofDays(3));
        return false;
    }

    @Override
    public Map<String, Integer> getHosts() {
        Map<String, Integer> map = new HashMap<>();
        likeKeys().stream().forEach(key -> {
            Object value = getValueByKey(key);
            if (value != null) {
                map.put(key.replaceAll(XConstant.HOST_PREFIX, ""), (Integer) value);
            }
        });
        return map;
    }

    private Set<String> likeKeys() {
        return redisTemplate.keys(XConstant.HOST_PREFIX + "*");
    }

    private String covertToCountHost(String host) {
        return XConstant.HOST_PREFIX + host;
    }

    private Object getValueByKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    private void setValue(String key, Object val, Duration timeout) {
        redisTemplate.opsForValue().set(key, val, timeout);
    }

    private void setValue(String key, Object val) {
        redisTemplate.opsForValue().set(key, val);
    }

    private boolean deleteByKey(String key) {
        return redisTemplate.delete(key);
    }

    private void incrementOnlineCount(String nettyUrl) {
        redisTemplate.boundValueOps(XConstant.REDIS_ONLINE_COUNT).increment();
        redisTemplate.boundValueOps(nettyUrl).increment();
    }

    private void decrementOnlineCount(String nettyUrl) {
        redisTemplate.boundValueOps(XConstant.REDIS_ONLINE_COUNT).decrement();
        redisTemplate.boundValueOps(nettyUrl).decrement();
    }
}
