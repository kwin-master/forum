package com.kwin.forum.service;

import com.kwin.forum.dao.DiscussPostMapper;
import com.kwin.forum.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    /**
     * userId等于0时查询所有帖子
     * @param userId
     * @param offset 每页起始帖子编号
     * @param limit 每页最大帖子数
     * @return
     */
    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit) {
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    /**
     * userId = 0 时查询所有帖子数量,否则查询某用户的帖子数
     * @Param 注解用来给参数取别名
     *
     * @param userId
     * @return
     */
    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }
}
