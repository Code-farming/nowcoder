package com.lhb.nowcoder.controller;

import com.lhb.nowcoder.entity.Event;
import com.lhb.nowcoder.entity.Page;
import com.lhb.nowcoder.entity.User;
import com.lhb.nowcoder.event.EventProducer;
import com.lhb.nowcoder.service.FollowService;
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

import java.util.List;
import java.util.Map;

import static com.lhb.nowcoder.util.NowCoderConstant.*;

@Controller
public class FollowController {
    @Resource
    private HostHolder hostHolder;

    @Resource
    private UserService userService;

    @Resource
    private FollowService followService;

    @Resource
    private EventProducer eventProducer;

    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        if (user == null) {
            return NowCoderUtil.getJsonString(1, "用户未登录");
        }
        followService.follow(user.getId(), entityType, entityId);

        // 触发关注事件
        Event event = new Event().setTopic(TOPIC_FOLLOW)
                .setUserId(user.getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireMessage(event);

        return NowCoderUtil.getJsonString(0, "已关注");
    }

    @RequestMapping(path = "/unFollow", method = RequestMethod.POST)
    @ResponseBody
    public String unFollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        if (user == null) {
            return NowCoderUtil.getJsonString(1, "用户未登录");
        }
        followService.unFollow(user.getId(), entityType, entityId);
        return NowCoderUtil.getJsonString(0, "已取消关注");
    }

    /**
     * 查找用户关注的用户列表
     *
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/getUserFollowList/{userId}", method = RequestMethod.GET)
    public String getUserFollowList(@PathVariable("userId") int userId, Page page, Model model) {
        // 1.查询用户是否存在
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user", user);

        // 2.设置分页信息
        page.setLimit(5);
        page.setPath("/getUserFollowList/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        // 3.查找用户关注的用户列表
        List<Map<String, Object>> userList = followService.findFolloweeList(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User targetUser = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(targetUser.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "/site/followee";
    }

    /**
     * 查找用户的粉丝列表
     *
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/getFollowerList/{userId}", method = RequestMethod.GET)
    public String getFollowerList(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(userId,ENTITY_TYPE_USER));

        List<Map<String, Object>> userList = followService.findFollowerList(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User targetUser = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(targetUser.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "/site/follower";
    }

    private boolean hasFollowed(Integer userId) {
        if (hostHolder.getUser() == null) {
            return false;
        }

        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }


}
