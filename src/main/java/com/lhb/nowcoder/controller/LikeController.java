package com.lhb.nowcoder.controller;

import com.lhb.nowcoder.entity.Event;
import com.lhb.nowcoder.entity.User;
import com.lhb.nowcoder.event.EventProducer;
import com.lhb.nowcoder.service.LikeService;
import com.lhb.nowcoder.util.HostHolder;
import com.lhb.nowcoder.util.NowCoderUtil;
import com.lhb.nowcoder.util.RedisKeyUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.lhb.nowcoder.util.NowCoderConstant.ENTITY_TYPE_POST;
import static com.lhb.nowcoder.util.NowCoderConstant.TOPIC_LIKE;

@Controller
public class LikeController {
    @Resource
    private HostHolder hostHolder;

    @Resource
    private LikeService likeService;

    @Resource
    private EventProducer eventProducer;

    @Resource
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        User user = hostHolder.getUser();
        if (user == null) {
            return NowCoderUtil.getJsonString(1, "用户未登录");
        }
        // 点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        // 数量
        long entityLikeCount = likeService.findEntityLikeCount(entityType, entityId);
        int entityLikeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("likeCount", entityLikeCount);
        result.put("likeStatus", entityLikeStatus);

        // 触发点赞事件
        if (entityLikeStatus == 1){
            Event event = new Event().setTopic(TOPIC_LIKE)
                    .setUserId(user.getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);
            eventProducer.fireMessage(event);

            if (entityType==ENTITY_TYPE_POST){
                // 计算帖子的分数
                String redisKey = RedisKeyUtils.getPostScoreKey();
                redisTemplate.opsForSet().add(redisKey, entityId);
            }
        }


        return NowCoderUtil.getJsonString(0, null, result);
    }

}
