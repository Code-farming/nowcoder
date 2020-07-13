package com.lhb.nowcoder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NowcoderApplication.class)
public class QuartzTest {
    @Resource
    private Scheduler scheduler;

    @Test
    public void testDeleteJob(){
        try {
            boolean bool = scheduler.deleteJob(new JobKey("alphaJob", "alphaGroup"));
            System.out.println(bool);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
