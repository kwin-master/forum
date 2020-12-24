package com.kwin.forum.service;

import com.kwin.forum.dao.LoginTicketMapper;
import com.kwin.forum.dao.UserMapper;
import com.kwin.forum.entity.LoginTicket;
import com.kwin.forum.entity.User;
import com.kwin.forum.util.MailClient;
import com.kwin.forum.util.RedisKeyUtil;
import com.kwin.forum.util.UUIDUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.kwin.forum.contants.ForumContent.*;
import static com.kwin.forum.contants.LoginTicketContent.*;
import static com.kwin.forum.contants.RoleContent.*;
import static com.kwin.forum.contants.UserContent.*;

@Service
public class UserService extends BaseService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${forum.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
//        return userMapper.selectById(id);
        User user = getCache(id);//先查缓存
        if (user == null) {
            user = initCache(id);//缓存没有，就初始化缓存
        }
        return user;
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
        user.setType(NORMAL_USER);
        user.setStatus(USER_INACTIVE);
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

    /**
     * 激活账号的业务
     * @param userId
     * @param code
     * @return
     */
    public int activation(int userId,String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            clearCache(userId);//防止数据双写不一致，先删缓存，再更新数据库
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 登录的业务
     * @return
     */
    public Map<String,Object> login(String username,String password,int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        //空值处理
        logger.info("空值检查");
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg","账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg","密码不能为空!");
            return map;
        }

        logger.info("验证账号");
        //验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg","该账号不存在!");
            return map;
        }
        //验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg","该账号未激活!");
            return map;
        }
        //验证密码
        password = UUIDUtils.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg","密码不正确!");
            return map;
        }

        logger.info("生成登录凭证,有效时间为" + expiredSeconds/60/60 + "h");
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(UUIDUtils.generateUUID());
        loginTicket.setStatus(TICKET_ACTIVE);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
//        loginTicketMapper.insertLoginTicket(loginTicket);

        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey,loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    /**
     * 退出登录
     * @param ticket
     */
    public void logout(String ticket) {
//        loginTicketMapper.updateStatus(ticket,TICKET_INACTIVE);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(TICKET_INACTIVE);
        redisTemplate.opsForValue().set(redisKey,loginTicket);
    }

    /**
     * 查询ticket，获取登录凭证
     * @param ticket
     * @return
     */
    public LoginTicket findLoginTicket(String ticket) {
//        return loginTicketMapper.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    public int updateHeader(int userId,String headerUrl) {
//        return userMapper.updateHeader(userId,headerUrl);
        clearCache(userId);//防止数据双写不一致，先删缓存，再更新数据库
        int rows = userMapper.updateHeader(userId,headerUrl);
        return rows;
    }

    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    /**
     * 从缓存中取用户
     * @param userId
     * @return
     */
    public User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 缓存中取不到用户,就初始化缓存
     * @param userId
     * @return
     */
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }

    /**
     * 数据变更，清空缓存
     * @param userId
     */
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.findUserById(userId);
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });

        return list;
    }
}
