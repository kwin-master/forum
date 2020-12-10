package com.kwin.forum.test;

import com.kwin.forum.ForumApplication;
import org.junit.Test;
import org.slf4j.LoggerFactory;

public class LogTest extends ForumApplication {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LogTest.class);

    @Test
    public void test() {
        log.trace("log trace here");
        log.debug("log debug here");
        log.info("log info here");
        log.warn("log warn here");
        log.error("log error here");
    }
}
