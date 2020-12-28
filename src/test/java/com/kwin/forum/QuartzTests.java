package com.kwin.forum;

import org.junit.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;

public class QuartzTests extends ForumApplicationTests {
    @Autowired
    private Scheduler scheduler;

    @Test
    public void testDeleteJob() {
        try {
            boolean result = scheduler.deleteJob(new JobKey("alphaJob","alphaJobGroup"));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
