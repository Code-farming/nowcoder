package com.lhb.nowcoder.dao;

import com.lhb.nowcoder.entity.Message;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * (Message)表数据库访问层
 *
 * @author LHb
 * @since 2020-07-08 12:15:57
 */
@Mapper
public interface MessageDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Message queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Message> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param message 实例对象
     * @return 对象列表
     */
    List<Message> queryAll(Message message);

    /**
     * 新增数据
     *
     * @param message 实例对象
     * @return 影响行数
     */
    int insert(Message message);

    /**
     * 修改数据
     *
     * @param message 实例对象
     * @return 影响行数
     */
    int update(Message message);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 查找用户会话的数量
     *
     * @param userId
     * @return 用户会话的数量
     */
    int selectConversationCount(Integer userId);

    /**
     * 查询当前用户的会话列表,针对每个会话只返回一条最新的私信.
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectConversations(@Param("userId") Integer userId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 查询某个会话所包含的私信数量
     *
     * @param conversationId
     * @return
     */
    int selectLetterCount(@Param("conversationId") String conversationId);

    /**
     * 查询某个会话未读的消息数量或者所有未读消息的数量
     *
     * @param userId
     * @param conversationId
     * @return
     */
    int selectLetterUnreadCount(@Param("userId") Integer userId, @Param("conversationId") String conversationId);

    List<Message> selectLetters(@Param("conversationId") String conversationId, @Param("offset") int offset, @Param("limit") int limit);

    void updateStatus(@Param("ids") List<Integer> ids, @Param("status") int status);

    Message selectLatestNotice(@Param("userId") int userId, @Param("topic") String topic);

    int selectNoticeCount(@Param("userId") int userId, @Param("topic") String topic);

    int selectNoticeUnReadCount(@Param("userId") int userId, @Param("topic") String topic);

    List<Message> selectNoticeList(@Param("userId") int userId, @Param("topic") String topic, @Param("offset") int offset, @Param("limit") int limit);
}