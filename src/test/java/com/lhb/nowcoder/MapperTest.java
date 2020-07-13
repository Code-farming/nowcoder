package com.lhb.nowcoder;

import com.lhb.nowcoder.dao.MessageDao;
import com.lhb.nowcoder.entity.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NowcoderApplication.class)
public class MapperTest {
    @Resource
    private MessageDao messageDao;

    @Test
    public void testSelectConversationCount() {
        int i = messageDao.selectConversationCount(111);
        System.out.println(i);
    }

    @Test
    public void testSelectConversations() {
        List<Message> messageList = messageDao.selectConversations(111, 0, 5);
        System.out.println(messageList);
    }

    @Test
    public void testSelectLetterCount() {
        int i = messageDao.selectLetterCount("111_112");
        System.out.println(i);
    }

    @Test
    public void testSelectLetterUnreadCount() {
        int unreadCountAll = messageDao.selectLetterUnreadCount(111, null);
        int unreadCountOne = messageDao.selectLetterUnreadCount(111, "111_112");
        System.out.println(unreadCountAll);
        System.out.println(unreadCountOne);
    }


}
