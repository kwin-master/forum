package com.kwin.forum.event;

import com.alibaba.fastjson.JSONObject;
import com.kwin.forum.entity.Event;
import com.kwin.forum.entity.Message;
import com.kwin.forum.service.MessageService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.kwin.forum.contants.TopicContent.*;
import static com.kwin.forum.contants.UserContent.SYSTEM_USER_ID;

/**
 * 事件消费者
 */
@Component
public class EventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    /**
     * 同时监听评论、点赞、关注三个主题
     * @param record
     */
    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }

        //发送站内通知
        Message message = new Message();
        message.setStatus(0);
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String,Object> content = new HashMap<>();
        content.put("userId",event.getUserId());//点赞、发评论、点关注的那个用户
        content.put("entityType",event.getEntityType());// 帖子、评论、用户
        content.put("entityId",event.getEntityId());

        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(),entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }
}
