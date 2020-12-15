package com.kwin.forum.service;

import com.kwin.forum.contants.ForumContent;
import com.kwin.forum.contants.UserContent;
import com.kwin.forum.dao.UserMapper;
import com.kwin.forum.entity.User;
import com.kwin.forum.util.MailClient;
import com.kwin.forum.util.UUIDUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService extends BaseService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${forum.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    public Map<String,Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        //空值处理
        logger.info("分析表单输入，判断是否符合标准");
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg","账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg","密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg","邮箱不能为空!");
            return map;
        }

        //验证账号
        logger.info("验证账号是否已存在");
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            logger.info("账号已存在!");
            map.put("usernameMsg","账号已存在!");
            return map;
        }

        //验证邮箱
        logger.info("验证邮箱是否已被使用");
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            logger.info("该邮箱已被注册");
            map.put("emailMsg","该邮箱已被注册");
            return map;
        }

        //注册用户
        logger.info("验证通过，开始往数据库中添加用户");
        user.setSalt(UUIDUtils.generateUUID().substring(0,5));
        user.setPassword(UUIDUtils.md5(user.getPassword() + user.getSalt()));
        user.setType(UserContent.NORMAL_USER);
        user.setStatus(UserContent.INACTIVE);
        user.setActivationCode(UUIDUtils.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送激活邮件
        logger.info("准备发送激活邮件到用户邮箱");
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        logger.info("激活连接:" + url);

        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);
        logger.info("激活连接已发送到用户邮箱");

        return map;
    }

    public int activation(int userId,String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ForumContent.ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId,1);
            return ForumContent.ACTIVATION_SUCCESS;
        } else {
            return ForumContent.ACTIVATION_FAILURE;
        }
    }
}
