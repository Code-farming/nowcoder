package com.lhb.nowcoder.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lhb.nowcoder.entity.DiscussPost;
import com.lhb.nowcoder.entity.Event;
import com.lhb.nowcoder.entity.Message;
import com.lhb.nowcoder.service.DiscussPostService;
import com.lhb.nowcoder.service.ElasticsearchService;
import com.lhb.nowcoder.service.MessageService;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.lhb.nowcoder.util.NowCoderConstant.*;

@Component
@Slf4j
public class EventConsumer {
    @Resource
    private MessageService messageService;

    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private ElasticsearchService elasticsearchService;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @Value("${wk.image.command}")
    private String wkImageCommand;


    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            log.error("消息的内容为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error("消息格式错误");
            return;
        }

        // 发布站内通知
        Message message = new Message();
        message.setStatus(0);
        message.setCreateTime(new Date());
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityId", event.getEntityId());
        content.put("entityType", event.getEntityType());
        if (event.getData() != null) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);

    }

    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            log.error("消息的内容为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error("消息格式错误");
            return;
        }

        DiscussPost discussPost = discussPostService.queryById(event.getEntityId());
        elasticsearchService.saveDiscussPost(discussPost);
    }

    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            log.error("消息的内容为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error("消息格式错误");
            return;
        }

        elasticsearchService.deleteDiscussPost(event.getEntityId());
    }

    @KafkaListener(topics = {TOPIC_SHARE})
    public void handleShareMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            log.error("消息的内容为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error("消息格式错误");
            return;
        }

        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        String command = wkImageCommand + " --quality 75 " + htmlUrl + " " + wkImageStorage + "/" + fileName + suffix;

        try {
            Runtime.getRuntime().exec(command);
            log.info("生成长图成功");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("生成长图失败");
        }

    }
}
