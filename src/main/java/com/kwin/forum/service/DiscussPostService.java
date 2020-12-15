package com.kwin.forum.service;

import com.kwin.forum.dao.DiscussPostMapper;
import com.kwin.forum.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService extends BaseService {
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
        if (userId == 0) {
            logger.info("查询出所有帖子,从第" + offset + "条开始，每页" + limit + "条记录");
        }else {
            logger.info("查询出userId=" + userId + "的所有帖子,从第" + offset + "条开始，每页" + limit + "条记录");
        }
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
        logger.info("查询帖子数量");
        int rows = discussPostMapper.selectDiscussPostRows(userId);
        if (userId == 0) {
            logger.info("一共有" + rows + "条帖子");
        } else {
            logger.info("userId=" + userId + "共有" + rows + "条帖子");
        }
        return rows;
    }
}
