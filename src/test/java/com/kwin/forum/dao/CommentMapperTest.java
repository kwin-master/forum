package com.kwin.forum.dao;

import com.kwin.forum.ForumApplicationTests;
import com.kwin.forum.entity.Comment;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kwin.forum.contants.CommentContent.*;

public class CommentMapperTest extends ForumApplicationTests {
    @Autowired
    private CommentMapper commentMapper;

    @Test
    public void selectCommentsByEntity() {
        List<Comment> comments = commentMapper.selectCommentsByEntity(ENTITY_TYPE_POST, 277, 0, 3);
        for (Comment comment : comments) {
            System.out.println(comment);
        }
    }

    @Test
    public void selectCountByEntity() {
        int rows = commentMapper.selectCountByEntity(ENTITY_TYPE_POST, 277);
        System.out.println(rows);
    }
}