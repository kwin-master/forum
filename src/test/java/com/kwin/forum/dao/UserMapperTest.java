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

    @Test
    public void selectByName() {
        User user = userMapper.selectByName("niuke");
        System.out.println(user);
    }

    @Test
    public void selectByEmail() {
        User user = userMapper.selectByEmail("nowcoder149@sina.com");
        System.out.println(user);
    }

    @Test
    public void insertUser() {
        User user = new User();
        user.setUsername("test150");
        user.setPassword("123456");
        System.out.println(user.getId());
        userMapper.insertUser(user);
        System.out.println(user.getId());
    }

    @Test
    public void deleteUser() {
        userMapper.deleteUser(150);
    }

    @Test
    public void updateStatus() {
        userMapper.updateStatus(149,1);
    }
}