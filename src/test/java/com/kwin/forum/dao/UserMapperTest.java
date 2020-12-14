package com.kwin.forum.dao;

import com.kwin.forum.ForumApplicationTests;
import com.kwin.forum.entity.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class UserMapperTest extends ForumApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void selectById() {
        User user = userMapper.selectById(149);
        System.out.println(user);
    }
}