package com.kwin.forum.controller;

import com.kwin.forum.entity.Event;
import com.kwin.forum.entity.User;
import com.kwin.forum.event.EventProducer;
import com.kwin.forum.service.LikeService;
import com.kwin.forum.util.HostHolder;
import com.kwin.forum.util.JsonUtils;
import com.kwin.forum.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

import static com.kwin.forum.contants.CommentContent.ENTITY_TYPE_POST;
import static com.kwin.forum.contants.TopicContent.TOPIC_LIKE;

@Controller
public class LikeController extends BaseController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping(path = "/like")
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId,int postId) {
        User user = hostHolder.getUser();

        //点赞
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        //点赞数
        long likeCount = likeService.findEntityLikeCount(entityType,entityId);
        //状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(),entityType,entityId);
        //返回的结果
        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        // 触发点赞事件
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }

        if (entityType == ENTITY_TYPE_POST) {
            //计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey,postId);
        }

        return JsonUtils.getJSONString(0,null,map);
    }
}
