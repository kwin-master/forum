package com.kwin.forum.dao;

import com.kwin.forum.ForumApplicationTests;
import com.kwin.forum.entity.LoginTicket;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static com.kwin.forum.contants.LoginTicketContent.TICKET_INACTIVE;
import static org.junit.Assert.*;

public class LoginTicketMapperTest extends ForumApplicationTests {
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void insertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(149);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(TICKET_INACTIVE);
        loginTicket.setExpired(new Date());
        System.out.println(loginTicket);
        int row = loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(loginTicket);
        System.out.println(row);
    }

    @Test
    public void selectByTicket() {
        LoginTicket ticket = loginTicketMapper.selectByTicket("abc");
        System.out.println(ticket);
    }

    @Test
    public void updateStatus() {
        loginTicketMapper.updateStatus("abc",0);
    }
}