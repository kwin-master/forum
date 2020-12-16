package com.kwin.forum.controller;

import com.kwin.forum.entity.DiscussPost;
import com.kwin.forum.entity.User;
import com.kwin.forum.service.DiscussPostService;
import com.kwin.forum.service.UserService;
import com.kwin.forum.util.HostHolder;
import com.kwin.forum.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.Date;

import static com.kwin.forum.contants.DiscussPostContent.*;

@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController extends BaseController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @PostMapping(path = "/add")
    @ResponseBody
    public String addDiscussPost(String title,String content) {
        logger.info("发布新帖子");
        User user = hostHolder.getUser();
        if (user == null) {
            return JsonUtils.getJSONString(403,"你还没有登录哦!");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCommentCount(0);
        post.setScore(0.0);
        post.setStatus(NORMAL_STATE);
        post.setType(NORMAL_TYPE);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        logger.info("帖子发布成功");
        //报错的情况，将来统一处理
        return JsonUtils.getJSONString(0,"发布成功!");
    }

    @GetMapping(path = "/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model) {
        logger.info("查看帖子详情");
        //帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        //作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        return "/site/discuss-detail";
    }
}
