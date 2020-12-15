package com.kwin.forum.dao;

import com.kwin.forum.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    void insertUser(User user);

    void deleteUser(int id);

    void updateStatus(int id,int status);
}
