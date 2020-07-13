package com.lhb.nowcoder.dao;

import com.lhb.nowcoder.entity.LoginTicket;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * (LoginTicket)表数据库访问层
 *
 * @author LHb
 * @since 2020-05-16 00:48:13
 */
@Mapper
@Deprecated
public interface LoginTicketDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    LoginTicket queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<LoginTicket> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param loginTicket 实例对象
     * @return 对象列表
     */
    List<LoginTicket> queryAll(LoginTicket loginTicket);

    /**
     * 新增数据
     *
     * @param loginTicket 实例对象
     * @return 影响行数
     */
    int insert(LoginTicket loginTicket);

    /**
     * 修改数据
     *
     * @param loginTicket 实例对象
     * @return 影响行数
     */
    int update(LoginTicket loginTicket);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    void updateStatus(@Param("ticket") String ticket, @Param("status") int status);

    LoginTicket selectByTicket(@Param("ticket") String ticket);
}