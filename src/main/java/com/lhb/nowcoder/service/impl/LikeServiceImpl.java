package com.lhb.nowcoder.service.impl;

import com.lhb.nowcoder.service.LikeService;
import com.lhb.nowcoder.util.RedisKeyUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LikeServiceImpl implements LikeService {
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void like(int userId, int entityType, int entityId,int entityUserId) {
//        String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType, entityId);
//        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
//        if (isMember) {
//            redisTemplate.opsForSet().remove(entityLikeKey, userId);
//        } else {
//            redisTemplate.opsForSet().add(entityLikeKey, userId);
//        }
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtils.getUserLikeKey(entityUserId);
                Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
                redisOperations.multi();
                if (isMember){
                    redisTemplate.opsForSet().remove(entityLikeKey,userId);
                    redisTemplate.opsForValue().decrement(userLikeKey);
                }else{
                    redisTemplate.opsForSet().add(entityLikeKey,userId);
                    redisTemplate.opsForValue().increment(userLikeKey);
                }
                return redisOperations.exec();
            }
        });
    }

    @Override
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType, entityId);
        Long size = redisTemplate.opsForSet().size(entityLikeKey);
        return size;
    }

    @Override
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType, entityId);
        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        return isMember ? 1 : 0;
    }

    @Override
    public Integer findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtils.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count;
    }


}
