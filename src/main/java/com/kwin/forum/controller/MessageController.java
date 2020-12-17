package com.kwin.forum.controller;

import com.kwin.forum.entity.Message;
import com.kwin.forum.entity.Page;
import com.kwin.forum.entity.User;
import com.kwin.forum.service.MessageService;
import com.kwin.forum.service.UserService;
import com.kwin.forum.util.HostHolder;
import com.kwin.forum.util.JsonUtils;
import com.kwin.forum.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class MessageController extends BaseController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //私信列表
    @GetMapping(path = "/letter/list")
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        //会话列表
        logger.info("查询userId=" + user.getId() + "的会话列表");
        List<Message> conversationList = messageService.findConversations(user.getId(),page.getOffset(),page.getLimit());
        List<Map<String,Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String,Object> map = new HashMap<>();
                map.put("conversation",message);
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target",userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);

        //查询未读消息数量
        logger.info("查询未读消息的数量");
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);

        return "/site/letter";
    }

    /**
     * 获取私信详情
     * @param conversationId
     * @param page
     * @param model
     * @return
     */
    @GetMapping(path = "/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Page page,Model model) {
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        //私信列表
        logger.info("查询conversationId=" + conversationId + "的私信列表");
        List<Message> letterList = messageService.findLetters(conversationId,page.getOffset(),page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String,Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);

        //私信目标
        model.addAttribute("target",getLetterTarget(conversationId));

        //设置已读
        logger.info("设置该打开的私信详情列表已读");
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    /**
     * 根据私信列表获取私信的ids
     * @param letterList
     * @return
     */
    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0)
                    ids.add(message.getId());
            }
        }

        return ids;
    }

    /**
     * 根据conversationId获取目标user
     * @param conversationId
     * @return
     */
    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    /**
     * 发送私信
     * @param toName
     * @param content
     * @return
     */
    @PostMapping(path = "/letter/send")
    @ResponseBody
    public String sendLetter(String toName,String content) {
        User target = userService.findUserByName(toName);
        if (target == null) {
            return JsonUtils.getJSONString(1,"目标用户不存在!");
        }

        logger.info(hostHolder.getUser().getUsername() + "发送私信到" + toName);
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        message.setStatus(0);
        messageService.addMessage(message);

        return JsonUtils.getJSONString(0);
    }
}
