package com.kwin.forum.controller;

import com.kwin.forum.entity.User;
import com.kwin.forum.service.LikeService;
import com.kwin.forum.util.HostHolder;
import com.kwin.forum.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController extends BaseController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping(path = "/like")
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId) {
        User user = hostHolder.getUser();
        logger.info(user.getUsername() + "点赞");
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

        return JsonUtils.getJSONString(0,null,map);
    }
}
