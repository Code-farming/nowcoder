package com.lhb.nowcoder.dao;

import com.lhb.nowcoder.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * (DiscussPost)表数据库访问层
 *
 * @author LHb
 * @since 2020-05-08 18:02:42
 */
@Mapper
public interface DiscussPostDao {

    int selectDiscussPostRows(@Param("userId") int userId);


    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    DiscussPost queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<DiscussPost> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param discussPost 实例对象
     * @return 对象列表
     */
    List<DiscussPost> queryAll(DiscussPost discussPost);

    /**
     * 新增数据
     *
     * @param discussPost 实例对象
     * @return 影响行数
     */
    int insert(DiscussPost discussPost);

    /**
     * 修改数据
     *
     * @param discussPost 实例对象
     * @return 影响行数
     */
    int update(DiscussPost discussPost);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);


    List<DiscussPost> selectDiscussPost(int userId, int offset, int limit);

    void updateCommentCount(@Param("id") Integer id, @Param("commentCount") int commentCount);

    void updateType(@Param("id") int id,@Param("type") int type);

    void updateStatus(@Param("id") int id,@Param("status") int status);

}