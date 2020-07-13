package com.lhb.nowcoder.service.impl;

import com.lhb.nowcoder.entity.User;
import com.lhb.nowcoder.service.FollowService;
import com.lhb.nowcoder.service.UserService;
import com.lhb.nowcoder.util.RedisKeyUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.lhb.nowcoder.util.NowCoderConstant.*;

@Service
public class FollowServiceImpl implements FollowService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserService userService;


    @Override
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey = RedisKeyUtils.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtils.getFollowerKey(entityType, entityId);

                redisOperations.multi();

                redisTemplate.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                redisTemplate.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                return redisOperations.exec();
            }
        });
    }

    @Override
    public void unFollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey = RedisKeyUtils.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtils.getFollowerKey(entityType, entityId);

                redisOperations.multi();

                redisTemplate.opsForZSet().remove(followeeKey, entityId, System.currentTimeMillis());
                redisTemplate.opsForZSet().remove(followerKey, userId, System.currentTimeMillis());

                return redisOperations.exec();
            }
        });
    }

    @Override
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtils.getFolloweeKey(userId, entityType);
        Long followCount = redisTemplate.opsForZSet().zCard(followeeKey);
        return followCount == null ? 0 : followCount;
    }

    @Override
    public long findFollowerCount(int entityId, int entityType) {
        String followerKey = RedisKeyUtils.getFollowerKey(entityType, entityId);
        Long followerCount = redisTemplate.opsForZSet().zCard(followerKey);
        return followerCount == null ? 0 : followerCount;
    }

    @Override
    public List<Map<String, Object>> findFolloweeList(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtils.getFolloweeKey(userId, ENTITY_TYPE_USER);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, limit);
        if (targetIds == null) {
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user", user);
            Double followTime = redisTemplate.opsForZSet().score(followeeKey, targetId);
            map.put("followTime", new Date(followTime.longValue()));
            list.add(map);
        }
        return list;
    }

    @Override
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtils.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    @Override
    public List<Map<String, Object>> findFollowerList(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtils.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer>  targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, limit);
        if (targetIds == null) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user", user);
            Double followTime = redisTemplate.opsForZSet().score(followerKey, targetId);
            map.put("followTime",new Date(followTime.longValue()));
            list.add(map);
        }

        return list;
    }


}
