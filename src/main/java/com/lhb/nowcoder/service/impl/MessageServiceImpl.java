package com.lhb.nowcoder.service.impl;

import com.lhb.nowcoder.entity.Message;
import com.lhb.nowcoder.dao.MessageDao;
import com.lhb.nowcoder.service.MessageService;
import com.lhb.nowcoder.util.SensitiveFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Message)表服务实现类
 *
 * @author LHb
 * @since 2020-07-08 12:15:57
 */
@Service("messageService")
public class MessageServiceImpl implements MessageService {
    @Resource
    private MessageDao messageDao;

    @Resource
    private SensitiveFilter sensitiveFilter;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Message queryById(Integer id) {
        return this.messageDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<Message> queryAllByLimit(int offset, int limit) {
        return this.messageDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param message 实例对象
     * @return 实例对象
     */
    @Override
    public Message insert(Message message) {
        this.messageDao.insert(message);
        return message;
    }

    /**
     * 修改数据
     *
     * @param message 实例对象
     * @return 实例对象
     */
    @Override
    public Message update(Message message) {
        this.messageDao.update(message);
        return this.queryById(message.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.messageDao.deleteById(id) > 0;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public int findConversationCount(Integer userId) {
        return messageDao.selectConversationCount(userId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Message> findConversations(Integer userId, int offset, int limit) {
        return messageDao.selectConversations(userId, offset, limit);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public int findLetterCount(String conversationId) {
        return messageDao.selectLetterCount(conversationId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public int findLetterUnreadCount(Integer userId, String conversationId) {
        return messageDao.selectLetterUnreadCount(userId, conversationId);
    }

    @Override
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageDao.selectLetters(conversationId, offset, limit);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void readMessage(List<Integer> letterIds) {
        messageDao.updateStatus(letterIds, 1);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        messageDao.insert(message);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Message findLatestNotice(int userId, String topic) {
        return messageDao.selectLatestNotice(userId, topic);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public int findNoticeCount(int userId, String topic) {
        return messageDao.selectNoticeCount(userId, topic);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public int findNoticeUnReadCount(int userId, String topic) {
        return messageDao.selectNoticeUnReadCount(userId, topic);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Message> findNoticeList(int userId, String topic, int offset, int limit) {
        return messageDao.selectNoticeList(userId, topic,offset,limit);
    }


}