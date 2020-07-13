package com.lhb.nowcoder.service;

import com.lhb.nowcoder.entity.DiscussPost;

import java.util.List;

/**
 * (DiscussPost)表服务接口
 *
 * @author LHb
 * @since 2020-05-08 18:02:42
 */
public interface DiscussPostService {


    int findDiscussPostRows(int userId);

    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);

    int addDiscussPost(DiscussPost post);


    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    DiscussPost queryById(Integer id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<DiscussPost> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param discussPost 实例对象
     * @return 实例对象
     */
    DiscussPost insert(DiscussPost discussPost);

    /**
     * 修改数据
     *
     * @param discussPost 实例对象
     * @return 实例对象
     */
    DiscussPost update(DiscussPost discussPost);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);


    void updateCommentCount(Integer entityId, int commentCount);

}