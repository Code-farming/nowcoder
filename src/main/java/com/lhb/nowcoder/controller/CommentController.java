package com.lhb.nowcoder.controller;

import com.lhb.nowcoder.entity.Comment;
import com.lhb.nowcoder.entity.DiscussPost;
import com.lhb.nowcoder.entity.Event;
import com.lhb.nowcoder.event.EventProducer;
import com.lhb.nowcoder.service.CommentService;
import com.lhb.nowcoder.service.DiscussPostService;
import com.lhb.nowcoder.util.HostHolder;
import com.lhb.nowcoder.util.RedisKeyUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.Date;

import static com.lhb.nowcoder.util.NowCoderConstant.*;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private HostHolder hostHolder;

    @Resource
    private CommentService commentService;

    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private EventProducer eventProducer;

    @Resource
    private RedisTemplate redisTemplate;



    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        if (comment.getTargetId() == null) {
            comment.setTargetId(0);
        }
        commentService.addComment(comment);

        // 触发评论通知
        Event event = new Event().setUserId(hostHolder.getUser().getId())
                .setTopic(TOPIC_COMMENT)
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        if (comment.getEntityType() == ENTITY_TYPE_POST){
            DiscussPost target = discussPostService.queryById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else if (comment.getEntityType() == ENTITY_TYPE_COMMENT){
            Comment target = commentService.queryById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }

        eventProducer.fireMessage(event);

        // 触发发帖事件
        if (comment.getEntityType() == ENTITY_TYPE_POST){
            Event event1 = new Event().setTopic(TOPIC_PUBLISH)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
             eventProducer.fireMessage(event1);

            // 计算帖子的分数
            String redisKey = RedisKeyUtils.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey,discussPostId);

        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
