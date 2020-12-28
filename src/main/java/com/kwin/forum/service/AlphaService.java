package com.kwin.forum.service;

import com.kwin.forum.dao.DiscussPostMapper;
import com.kwin.forum.dao.UserMapper;
import com.kwin.forum.entity.DiscussPost;
import com.kwin.forum.entity.User;
import com.kwin.forum.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

@Service
public class AlphaService extends BaseService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    //声明式事务
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public Object save1() {
        //新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(UUIDUtils.generateUUID().substring(0,5));
        user.setPassword(UUIDUtils.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://images.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello");
        post.setContent("新人报道!");
        discussPostMapper.insertDiscussPost(post);

        int i = 1 / 0;

        return "ok";
    }

    //编程式事务
    public Object save2() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                //新增用户
                User user = new User();
                user.setUsername("alpha");
                user.setSalt(UUIDUtils.generateUUID().substring(0,5));
                user.setPassword(UUIDUtils.md5("123" + user.getSalt()));
                user.setEmail("alpha@qq.com");
                user.setHeaderUrl("http://images.nowcoder.com/head/99t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                //新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("Hello");
                post.setContent("新人报道!");
                discussPostMapper.insertDiscussPost(post);

                int i = 1 / 0;

                return "ok";
            }
        });
    }

    //让该方法在多线程环境下，被异步调用
    @Async
    public void execute1() {
        logger.info("execute1执行了");
    }

    @Scheduled(initialDelay = 10000,fixedRate = 1000)
    public void execute2() {
        logger.info("execute2执行了");
    }
}
