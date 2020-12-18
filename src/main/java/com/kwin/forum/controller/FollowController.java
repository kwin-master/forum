package com.kwin.forum.controller;

import com.kwin.forum.entity.User;
import com.kwin.forum.service.FollowService;
import com.kwin.forum.util.HostHolder;
import com.kwin.forum.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FollowController extends BaseController {
    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping(path = "/follow")
    @ResponseBody
    public String follow(int entityType,int entityId) {
        User user = hostHolder.getUser();
        followService.follow(user.getId(),entityType,entityId);
        return JsonUtils.getJSONString(0,"已关注!");
    }

    @PostMapping(path = "/unfollow")
    @ResponseBody
    public String unfollow(int entityType,int entityId) {
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(),entityType,entityId);
        return JsonUtils.getJSONString(0,"已取消关注!");
    }
}
