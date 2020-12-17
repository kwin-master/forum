package com.kwin.forum.dao;

import com.kwin.forum.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    //根据实体类型和实体id查评论列表
    List<Comment> selectCommentsByEntity(int entityType,int entityId,int offset,int limit);

    //根据实体类型和实体id查评论数
    int selectCountByEntity(int entityType,int entityId);
}
