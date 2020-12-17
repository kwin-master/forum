package com.kwin.forum.dao;

import com.kwin.forum.ForumApplicationTests;
import com.kwin.forum.entity.Message;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class MessageMapperTest extends ForumApplicationTests {
    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void selectConversations() {
        List<Message> list = messageMapper.selectConversations(111,0,20);
        for (Message message : list) {
            System.out.println(message);
        }
    }

    @Test
    public void selectConversationCount() {
        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);
    }

    @Test
    public void selectLetters() {
        List<Message> list = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : list) {
            System.out.println(message);
        }
    }

    @Test
    public void selectLetterCount() {
        int count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);
    }

    @Test
    public void selectLetterUnreadCount() {
        int count = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(count);
    }
}