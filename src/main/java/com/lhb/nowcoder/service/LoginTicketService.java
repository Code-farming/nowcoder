package com.lhb.nowcoder.service;

import com.lhb.nowcoder.entity.LoginTicket;
import java.util.List;

/**
 * (LoginTicket)表服务接口
 *
 * @author LHb
 * @since 2020-05-16 00:48:13
 */
public interface LoginTicketService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    LoginTicket queryById(Integer id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<LoginTicket> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param loginTicket 实例对象
     * @return 实例对象
     */
    LoginTicket insert(LoginTicket loginTicket);

    /**
     * 修改数据
     *
     * @param loginTicket 实例对象
     * @return 实例对象
     */
    LoginTicket update(LoginTicket loginTicket);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}