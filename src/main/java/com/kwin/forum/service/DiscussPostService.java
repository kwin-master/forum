package com.kwin.forum.service;

import com.kwin.forum.dao.DiscussPostMapper;
import com.kwin.forum.entity.DiscussPost;
import com.kwin.forum.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService extends BaseService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

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

    /**
     * 添加帖子
     * @param post
     */
    public int addDiscussPost(DiscussPost post) {
        logger.info("添加帖子");
        if (post == null) {
            logger.error("参数不能为空");
            throw new IllegalArgumentException("参数不能为空!");
        }

        //转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    /**
     * 通过id查询帖子
     * @param id
     * @return
     */
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id,int commentCount) {
        return discussPostMapper.updateCommentCount(id,commentCount);
    }

    public int updateType(int id,int type) {
        return discussPostMapper.updateType(id,type);
    }

    public int updateStatus(int id,int status) {
        return discussPostMapper.updateStatus(id,status);
    }
}
