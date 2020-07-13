package com.lhb.nowcoder.service;

import com.lhb.nowcoder.entity.LoginTicket;
import com.lhb.nowcoder.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * (User)表服务接口
 *
 * @author LHb
 * @since 2020-05-08 18:04:19
 */
public interface UserService {

    User findUserById(int userId);

    Map<String, Object> register(User user);

    int activation(int userId, String code);

    Map<String, Object> login(String username, String password, int expiredSeconds);

    void logout(String ticket);


    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    User queryById(Integer id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<User> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    User insert(User user);

    /**
     * 修改数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    User update(User user);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);


    LoginTicket findUserByTicket(String ticket);

    int updateHeader(Integer id, String headerImgUrl);

    Map<String,Object> updatePassword(String oldPassword, String newPassword, String confirmPassword);

    User findUserByName(String username);

    Collection<? extends GrantedAuthority> getAuthorities(int userId);


}