package com.lhb.nowcoder.controller;

import com.alibaba.fastjson.JSONObject;
import com.lhb.nowcoder.entity.Message;
import com.lhb.nowcoder.entity.Page;
import com.lhb.nowcoder.entity.User;
import com.lhb.nowcoder.service.MessageService;
import com.lhb.nowcoder.service.UserService;
import com.lhb.nowcoder.util.HostHolder;
import com.lhb.nowcoder.util.NowCoderUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.*;

import static com.lhb.nowcoder.util.NowCoderConstant.*;

/**
 * (Message)表控制层
 *
 * @author LHb
 * @since 2020-07-08 12:15:57
 */
@Controller
public class MessageController {
    /**
     * 服务对象
     */
    @Resource
    private MessageService messageService;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private UserService userService;

    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        // 会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        // 返回给私信页面的信息
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                // 会话的第一条消息
                map.put("conversation", message);
                // 会话的消息数量
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                // 会话的未读消息数量
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                // 会话的目标对象
                int targetId = (user.getId().equals(message.getFromId()) ? message.getToId() : message.getFromId());
                map.put("target", userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        // 查询用户未读的消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnReadCount = messageService.findNoticeUnReadCount(user.getId(), null);
        model.addAttribute("noticeUnReadCount",noticeUnReadCount);

        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable String conversationId, Page page, Model model) {
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // 私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);

        // 私信目标
        model.addAttribute("target", getLetterTarget(conversationId));

        List<Integer> letterIds = getLetterIds(letterList);
        if (!letterIds.isEmpty()) {
            messageService.readMessage(letterIds);
        }

        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId().equals(id0)) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    private List<Integer> getLetterIds(List<Message> letterList) {
        User user = hostHolder.getUser();
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                if (hostHolder.getUser().getId().equals(message.getToId()) && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User target = userService.findUserByName(toName);
        if (target == null) {
            return NowCoderUtil.getJsonString(1, "目标用户不存在!");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        message.setStatus(0);
        messageService.addMessage(message);

        return NowCoderUtil.getJsonString(0);
    }

    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public String getNoticeList(Model model) {
        User user = hostHolder.getUser();
        if (user == null) {
            throw new RuntimeException("用户未登录");
        }

        // 1.查询评论类通知
        Map<String, Object> commentNoticeVO = getMessageVoByTopic(user.getId(), TOPIC_COMMENT);
        // 2.查询点赞类通知
        Map<String, Object> likeNoticeVO = getMessageVoByTopic(user.getId(), TOPIC_LIKE);
        // 3.查询关注类通知
        Map<String, Object> followNoticeVO = getMessageVoByTopic(user.getId(), TOPIC_FOLLOW);

        model.addAttribute("commentNotice",commentNoticeVO);
        model.addAttribute("likeNotice",likeNoticeVO);
        model.addAttribute("followNotice",followNoticeVO);

        // 4.查询未读消息的数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        // 5.查询未读通知的数量
        int noticeUnReadCount = messageService.findNoticeUnReadCount(user.getId(), null);
        model.addAttribute("noticeUnReadCount",noticeUnReadCount);

        return "/site/notice";
    }

    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String getNoticeList(@PathVariable String topic,Model model, Page page) {
        User user = hostHolder.getUser();

        // 设置分页信息
        page.setRows(messageService.findNoticeCount(user.getId(),topic));
        page.setLimit(5);
        page.setPath("/notice/detail/"+topic);

        List<Message> noticeList = messageService.findNoticeList(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String,Object>> noticeVOList = new ArrayList<>();
        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String,Object> map = new HashMap<>();
                // 通知
                map.put("notice",notice);
                // 内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map data = JSONObject.parseObject(content, HashMap.class);
                map.put("user",userService.findUserById((Integer) data.get("userId")));
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId",data.get("postId"));
                // 通知作者
                map.put("fromUser",userService.findUserById(notice.getFromId()));
                noticeVOList.add(map);
            }
        }

        model.addAttribute("notices",noticeVOList);

        // 设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/notice-detail";
    }

    private Map<String, Object> getMessageVoByTopic(int userId, String topic) {
        Map<String, Object> messageVO = new HashMap<>();
        Message message = messageService.findLatestNotice(userId, topic);
        if (message == null) {
            return null;
        }
        messageVO.put("message",message);
        String content = HtmlUtils.htmlUnescape(message.getContent());
        Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
        messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
        messageVO.put("entityType", data.get("entityType"));
        messageVO.put("entityId", data.get("entityId"));
        if (data.get("postId") != null) {
            messageVO.put("postId", data.get("postId"));
        }

        int NoticeCount = messageService.findNoticeCount(userId, topic);
        int NoticeUnReadCount = messageService.findNoticeUnReadCount(userId, topic);
        messageVO.put("count", NoticeCount);
        messageVO.put("unread", NoticeUnReadCount);
        return messageVO;
    }


}

