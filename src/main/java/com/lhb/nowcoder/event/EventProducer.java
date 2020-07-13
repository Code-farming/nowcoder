package com.lhb.nowcoder.event;

import com.alibaba.fastjson.JSONObject;
import com.lhb.nowcoder.entity.Event;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class EventProducer {
    @Resource
    private KafkaTemplate kafkaTemplate;

    // 处理事件
    public void fireMessage(Event event){
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
