package com.lhb.nowcoder.service.impl;

import com.lhb.nowcoder.dao.UserDao;
import com.lhb.nowcoder.entity.LoginTicket;
import com.lhb.nowcoder.entity.User;
import com.lhb.nowcoder.service.UserService;
import com.lhb.nowcoder.util.HostHolder;
import com.lhb.nowcoder.util.MailClient;
import com.lhb.nowcoder.util.NowCoderUtil;
import com.lhb.nowcoder.util.RedisKeyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.lhb.nowcoder.util.NowCoderConstant.*;

/**
 * (User)表服务实现类
 *
 * @author LHb
 * @since 2020-05-08 18:04:19
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

//    @Resource
//    private LoginTicketDao loginTicketDao;

    @Resource
    private TemplateEngine templateEngine;

    @Resource
    private MailClient mailClient;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private RedisTemplate redisTemplate;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    @Override
    public User findUserById(int userId) {
//        return userDao.queryById(userId);
        User user = getCache(userId);
        if (user == null) {
            user = initCache(userId);
        }
        return user;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        // 验证账号
        User u = userDao.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }

        // 验证邮箱
        u = userDao.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册!");
            return map;
        }

        // 注册用户
        user.setSalt(NowCoderUtil.generateUUID().substring(0, 5));
        user.setPassword(NowCoderUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(NowCoderUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userDao.insert(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        System.out.println(url);
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    @Override
    public int activation(int userId, String code) {
        User user = userDao.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userDao.updateStatus(userId, 1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }


    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public User queryById(Integer id) {
        return this.userDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<User> queryAllByLimit(int offset, int limit) {
        return this.userDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @Override
    public User insert(User user) {
        this.userDao.insert(user);
        return user;
    }

    /**
     * 修改数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @Override
    public User update(User user) {
        this.userDao.update(user);
        clearCache(user.getId());
        return this.queryById(user.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.userDao.deleteById(id) > 0;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public LoginTicket findUserByTicket(String ticket) {
//        LoginTicket loginTicket = loginTicketDao.selectByTicket(ticket);
        String ticketKey = RedisKeyUtils.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        return loginTicket;
    }

    @Override
    public int updateHeader(Integer id, String headerImgUrl) {
        int rows = userDao.updateHeaderUrl(id, headerImgUrl);
        clearCache(id);
        return rows;
    }

    @Override
    public Map<String, Object> updatePassword(String oldPassword, String newPassword, String confirmPassword) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(oldPassword)){
            map.put("oldPasswordError", "原密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(newPassword)){
            map.put("newPasswordError", "新密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(oldPassword)){
            map.put("confirmPasswordError", "确认密码不能为空!");
            return map;
        }

        if (!newPassword.equals(confirmPassword)){
            map.put("confirmPasswordError", "两次密码不一致!");
            return map;
        }
        User user = hostHolder.getUser();
        oldPassword = NowCoderUtil.md5(oldPassword+user.getSalt());
        if (!user.getPassword().equals(oldPassword)){
            map.put("oldPasswordError", "原密码错误!");
            return map;
        }

        newPassword = NowCoderUtil.md5(newPassword+user.getSalt());
        userDao.updatePassword(user.getId(),newPassword);
        clearCache(user.getId());
        return null;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public User findUserByName(String username) {
        return userDao.selectByName(username);
    }


    /**
     * 用户登录方法
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        // 验证账号
        User user = userDao.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活!");
            return map;
        }

        // 验证密码
        password = NowCoderUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setTicket(NowCoderUtil.generateUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
//        loginTicketDao.insert(loginTicket);
        // 使用redis存储ticket
        String ticketKey = RedisKeyUtils.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey,loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void logout(String ticket) {
//        loginTicketDao.updateStatus(ticket,1);
        // 使用redis修改ticket的状态
        String ticketKey = RedisKeyUtils.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
    }

    // redis优化性能
    // 1.优先从缓存中取值
    private User getCache(int userId){
        String redisKey = RedisKeyUtils.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    // 2.取不到数据时初始化缓存数据
    private User initCache(int userId){
        User user = userDao.selectById(userId);
        String userKey = RedisKeyUtils.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    // 3.数据变更时清空缓存数据
    private void clearCache(int userId){
        String userKey = RedisKeyUtils.getUserKey(userId);
        redisTemplate.delete(userKey);
    }
}