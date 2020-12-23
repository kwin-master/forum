package com.kwin.forum.controller;

import com.kwin.forum.entity.Comment;
import com.kwin.forum.entity.DiscussPost;
import com.kwin.forum.entity.Event;
import com.kwin.forum.event.EventProducer;
import com.kwin.forum.service.CommentService;
import com.kwin.forum.service.DiscussPostService;
import com.kwin.forum.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

import static com.kwin.forum.contants.CommentContent.ENTITY_TYPE_COMMENT;
import static com.kwin.forum.contants.CommentContent.ENTITY_TYPE_POST;
import static com.kwin.forum.contants.TopicContent.TOPIC_COMMENT;
import static com.kwin.forum.contants.TopicContent.TOPIC_PUBLISH;

@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;


    @PostMapping(path = "/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)//主题-评论
                .setUserId(hostHolder.getUser().getId())//登录用户id
                .setEntityType(comment.getEntityType())//实体-帖子/评论
                .setEntityId(comment.getEntityId())//评论id/帖子id
                .setData("postId", discussPostId);//帖子id
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        //评论事件发送到kafka的评论topic中
        eventProducer.fireEvent(event);

        //触发发帖事件
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
