package com.kwin.forum.dao;

import com.kwin.forum.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    /**
     * userId等于0时查询所有帖子
     * @param userId
     * @param offset 每页起始帖子编号
     * @param limit 每页最大帖子数
     * @return
     */
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

    /**
     * userId = 0 时查询所有帖子数量,否则查询某用户的帖子数
     * @Param 注解用来给参数取别名
     *
     * @param userId
     * @return
     */
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * 新增帖子
     * @param discussPost
     * @return
     */
    int insertDiscussPost(DiscussPost discussPost);

    /**
     * 根据id查询帖子
     * @param id
     * @return
     */
    DiscussPost selectDiscussPostById(int id);

    //更新评论数
    int updateCommentCount(int id,int commentCount);

    //更新帖子类型
    int updateType(int id,int type);

    //更新帖子状态
    int updateStatus(int id,int status);
}
