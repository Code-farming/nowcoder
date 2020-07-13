package com.lhb.nowcoder.service;

import com.lhb.nowcoder.entity.Message;
import java.util.List;

/**
 * (Message)表服务接口
 *
 * @author LHb
 * @since 2020-07-08 12:15:57
 */
public interface MessageService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Message queryById(Integer id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<Message> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param message 实例对象
     * @return 实例对象
     */
    Message insert(Message message);

    /**
     * 修改数据
     *
     * @param message 实例对象
     * @return 实例对象
     */
    Message update(Message message);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

    /**
     * 查找当前用户的会话数量
     * @param userId
     * @return
     */
    int findConversationCount(Integer userId);

    /**
     * 分页查询会话的第一消息
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> findConversations(Integer userId, int offset, int limit);

    /**
     * 查询一个会话的消息数量
     * @param conversationId
     * @return
     */
    int findLetterCount(String conversationId);

    /**
     * 查询一个会话的未读消息数量
     * @param userId
     * @param conversationId
     * @return
     */
    int findLetterUnreadCount(Integer userId, String conversationId);

    /**
     * 分页查找会话的详细的信息列表
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> findLetters(String conversationId, int offset, int limit);

    /**
     * 读消息
     * @param letterIds
     */
    void readMessage(List<Integer> letterIds);

    /**
     * 添加消息
     * @param message
     */
    void addMessage(Message message);

    /**
     * 查找某个主题下最新的通知
     * @param userId
     * @param topic
     * @return
     */
    Message findLatestNotice(int userId, String topic);

    /**
     * 查找某个主题所包含的通知数量
     * @param userId
     * @param topic
     * @return
     */
    int findNoticeCount(int userId,String topic);

    /**
     * 查找某个主题所包含的未读通知的数量
     * @param userId
     * @param topic
     * @return
     */
    int findNoticeUnReadCount(int userId,String topic);

    /**
     * 分页查找某个主题所包含的通知列表
     * @param userId
     * @param topic
     * @return
     */
    List<Message> findNoticeList(int userId,String topic,int offset,int limit);

}