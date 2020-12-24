package com.kwin.forum.controller;

import com.kwin.forum.entity.DiscussPost;
import com.kwin.forum.entity.Page;
import com.kwin.forum.entity.User;
import com.kwin.forum.service.DiscussPostService;
import com.kwin.forum.service.LikeService;
import com.kwin.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kwin.forum.contants.CommentContent.ENTITY_TYPE_POST;

@Controller
public class HomeController extends BaseController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping(path = "/index")
    public String getIndexPage(Model model, Page page) {
        logger.info("查询出首页的帖子");
        //springMVC会自动实例化Model和Page，并将Page注入Model
        //所以，在thymeleaf中可以直接访问Page对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");
        logger.info("初始化首页");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost discussPost : list) {
                Map<String,Object> map = new HashMap<>();
                map.put("post",discussPost);
                User user = userService.findUserById(discussPost.getUserId());
                map.put("user",user);

                //查询点赞的数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPost.getId());
                map.put("likeCount",likeCount);

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }

    @GetMapping(path = "/error")
    public String getErrorPage() {
        return "/error/500";
    }

    // 拒绝访问时的提示页面
    @GetMapping(path = "/denied")
    public String getDeniedPage() {
        return "/error/404";
    }
}
