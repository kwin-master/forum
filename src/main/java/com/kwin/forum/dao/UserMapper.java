package com.kwin.forum.dao;

import com.kwin.forum.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectById(int id);
}
