package com.kwin.forum.dao;

import com.kwin.forum.ForumApplication;
import com.kwin.forum.ForumApplicationTests;
import com.kwin.forum.entity.DiscussPost;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class DiscussPostMapperTest extends ForumApplicationTests {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void selectDiscussPosts() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }
    }

    @Test
    public void selectDiscussPostRows() {
        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }

    @Test
    public void insertDiscussPost() {
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(999);
        discussPost.setTitle("测试");
        discussPostMapper.insertDiscussPost(discussPost);
    }

    @Test
    public void selectDiscussPostById() {
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(280);
        System.out.println(discussPost);
    }
}