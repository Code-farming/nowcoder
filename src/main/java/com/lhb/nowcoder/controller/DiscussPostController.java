package com.lhb.nowcoder.controller;


import com.lhb.nowcoder.entity.*;
import com.lhb.nowcoder.event.EventProducer;
import com.lhb.nowcoder.service.CommentService;
import com.lhb.nowcoder.service.DiscussPostService;
import com.lhb.nowcoder.service.LikeService;
import com.lhb.nowcoder.service.UserService;
import com.lhb.nowcoder.util.HostHolder;
import com.lhb.nowcoder.util.NowCoderUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

import static com.lhb.nowcoder.util.NowCoderConstant.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Resource
    private HostHolder hostHolder;

    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private UserService userService;

    @Resource
    private CommentService commentService;

    @Resource
    private LikeService likeService;

    @Resource
    private EventProducer eventProducer;

    /**
     * 添加评论的接口
     *
     * @param title
     * @param content
     * @return
     */
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return NowCoderUtil.getJsonString(403, "你还没有登录哦!");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        post.setCommentCount(0);
        post.setStatus(0);
        post.setType(0);
        post.setScore(0);
        discussPostService.addDiscussPost(post);

        // 触犯发帖事件
        Event event =new Event().setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireMessage(event);

        // 报错的情况,将来统一处理.
        return NowCoderUtil.getJsonString(0, "发布成功!");
    }

    /**
     * @param discussPostId
     * @param model
     * @return
     */
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        // 1.根据discussPostId查找帖子
        DiscussPost post = discussPostService.queryById(discussPostId);
        model.addAttribute("post", post);

        // 2.根据查找到的帖子查找帖子的发布者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        // 3.查找帖子的点赞数
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeCount", likeCount);

        // 4.查找当前用户对该帖子的点赞状态
        User currentUser = hostHolder.getUser();
        int likeStatus = 0;
        if (currentUser == null) {
            likeStatus = 0;
        } else {
            likeStatus = likeService.findEntityLikeStatus(currentUser.getId(),ENTITY_TYPE_POST,post.getId());
        }
        model.addAttribute("likeStatus",likeStatus);


        // 5.设置评论的分页信息
        page.setRows(post.getCommentCount());
        page.setPath("/discuss/detail/" + discussPostId);
        page.setLimit(5);

        // 6.查找帖子对应的评论
        // 评论: 对帖子的评论
        // 回复: 对评论的评论
        // 评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());

        // 评论的VO列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 评论VO
                Map<String, Object> commentVo = new HashMap<>();
                // 评论
                commentVo.put("comment", comment);
                // 作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                // 点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount",likeCount);
                // 点赞状态
                if (currentUser == null) {
                    likeStatus = 0;
                } else {
                    likeStatus = likeService.findEntityLikeStatus(currentUser.getId(),ENTITY_TYPE_COMMENT,comment.getId());
                }
                commentVo.put("likeStatus",likeStatus);

                // 回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);
                        // 点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount",likeCount);
                        // 点赞状态
                        if (currentUser == null) {
                            likeStatus = 0;
                        } else {
                            likeStatus = likeService.findEntityLikeStatus(currentUser.getId(),ENTITY_TYPE_COMMENT,reply.getId());
                        }
                        replyVo.put("likeStatus",likeStatus);

                        replyVoList.add(replyVo);
                    }
                }

                commentVo.put("replys", replyVoList);
                // 设置回复的数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);

        System.out.println(commentVoList);
        return "/site/discuss-detail";
    }
}
