package com.kwin.forum.service;

import com.kwin.forum.ForumApplicationTests;
import com.kwin.forum.entity.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class UserServiceTest extends ForumApplicationTests {
    @Autowired
    private UserService userService;

    @Test
    public void findUserById() {
        User user = userService.findUserById(1);
        System.out.println(user);
    }
}