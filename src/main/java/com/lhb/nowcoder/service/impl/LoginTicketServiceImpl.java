package com.lhb.nowcoder.service.impl;

import com.lhb.nowcoder.entity.LoginTicket;
import com.lhb.nowcoder.dao.LoginTicketDao;
import com.lhb.nowcoder.service.LoginTicketService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (LoginTicket)表服务实现类
 *
 * @author LHb
 * @since 2020-05-16 00:48:13
 */
@Service("loginTicketService")
public class LoginTicketServiceImpl implements LoginTicketService {
    @Resource
    private LoginTicketDao loginTicketDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public LoginTicket queryById(Integer id) {
        return this.loginTicketDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<LoginTicket> queryAllByLimit(int offset, int limit) {
        return this.loginTicketDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param loginTicket 实例对象
     * @return 实例对象
     */
    @Override
    public LoginTicket insert(LoginTicket loginTicket) {
        this.loginTicketDao.insert(loginTicket);
        return loginTicket;
    }

    /**
     * 修改数据
     *
     * @param loginTicket 实例对象
     * @return 实例对象
     */
    @Override
    public LoginTicket update(LoginTicket loginTicket) {
        this.loginTicketDao.update(loginTicket);
        return this.queryById(loginTicket.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.loginTicketDao.deleteById(id) > 0;
    }
}