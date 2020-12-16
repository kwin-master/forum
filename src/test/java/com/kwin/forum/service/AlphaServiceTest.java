package com.kwin.forum.service;

import com.kwin.forum.ForumApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class AlphaServiceTest extends ForumApplicationTests {
    @Autowired
    private AlphaService alphaService;

    @Test
    public void save1() {
        Object obj = alphaService.save1();
        System.out.println(obj);
    }

    @Test
    public void save2() {
        Object obj = alphaService.save2();
        System.out.println(obj);
    }
}