package com.lhb.nowcoder.service;

import com.lhb.nowcoder.entity.Comment;
import java.util.List;

/**
 * (Comment)表服务接口
 *
 * @author LHb
 * @since 2020-05-08 17:43:18
 */
public interface CommentService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Comment queryById(Integer id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<Comment> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param comment 实例对象
     * @return 实例对象
     */
    Comment insert(Comment comment);

    /**
     * 修改数据
     *
     * @param comment 实例对象
     * @return 实例对象
     */
    Comment update(Comment comment);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);


    List<Comment> findCommentsByEntity(int entityType, Integer entityId, int offset, int limit);

    int findCommentCount(int entityType, Integer entityId);

    int addComment(Comment comment);
}