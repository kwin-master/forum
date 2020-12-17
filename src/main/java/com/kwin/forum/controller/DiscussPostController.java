package com.kwin.forum.controller;

import com.kwin.forum.entity.Comment;
import com.kwin.forum.entity.DiscussPost;
import com.kwin.forum.entity.Page;
import com.kwin.forum.entity.User;
import com.kwin.forum.service.CommentService;
import com.kwin.forum.service.DiscussPostService;
import com.kwin.forum.service.UserService;
import com.kwin.forum.util.HostHolder;
import com.kwin.forum.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.*;

import static com.kwin.forum.contants.CommentContent.ENTITY_TYPE_COMMENT;
import static com.kwin.forum.contants.CommentContent.ENTITY_TYPE_POST;
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

    @Autowired
    private CommentService commentService;

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
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        logger.info("查看帖子详情");
        //帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        //作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        //评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        //评论:给帖子的评论
        //回复:给评论的评论
        //评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST,post.getId(),page.getOffset(),page.getLimit());

        //评论VO列表
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                //评论VO
                Map<String,Object> commentVO = new HashMap<>();
                //评论
                commentVO.put("comment",comment);
                //作者
                commentVO.put("user",userService.findUserById(comment.getUserId()));
                //回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
                //回复VO列表
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String,Object> replyVo = new HashMap<>();
                        //回复
                        replyVo.put("reply",reply);
                        //作者
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);

                        replyVoList.add(replyVo);
                    }
                }
                commentVO.put("replys",replyVoList);

                //回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVO.put("replyCount",replyCount);

                commentVoList.add(commentVO);
            }
        }
        model.addAttribute("comments",commentVoList);

        return "/site/discuss-detail";
    }
}
