package com.kwin.forum.util;

import com.kwin.forum.ForumApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class SensitiveFilterTest extends ForumApplicationTests {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "这里面可以赌☆博，可以嫖☆娼，可以吸☆毒，可以开☆票，哈哈哈！";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}