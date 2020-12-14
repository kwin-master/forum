package com.kwin.forum.util;

import com.kwin.forum.ForumApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.junit.Assert.*;

public class MailClientTest extends ForumApplicationTests {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void sendMail() {
        mailClient.sendMail("kwin361@foxmail.com","论坛邮件测试","Welcome");
    }

    @Test
    public void testHtmlMail() {
        Context context = new Context();
        context.setVariable("username","sunday");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("kwin361@foxmail.com","HTML",content);
    }
}