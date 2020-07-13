package com.lhb.nowcoder;

import com.lhb.nowcoder.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NowcoderApplication.class)
public class MailTests {

    @Resource
    private MailClient mailClient;

    @Resource
    private TemplateEngine templateEngine;

    @Test
    public void testMailClient(){
        mailClient.sendMail("1650243281@qq.com","test","welcome");
    }

    @Test
    public void testHtmlMail() {
        Context context = new Context();
        context.setVariable("username", "sunday");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("1650243281@qq.com", "HTML", content);
    }
}
