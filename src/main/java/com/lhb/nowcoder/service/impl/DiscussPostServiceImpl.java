package com.lhb.nowcoder.service.impl;

import com.lhb.nowcoder.entity.DiscussPost;
import com.lhb.nowcoder.dao.DiscussPostDao;
import com.lhb.nowcoder.service.DiscussPostService;
import com.lhb.nowcoder.util.SensitiveFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * (DiscussPost)表服务实现类
 *
 * @author LHb
 * @since 2020-05-08 18:02:42
 */
@Service("discussPostService")
public class DiscussPostServiceImpl implements DiscussPostService {
    @Resource
    private DiscussPostDao discussPostDao;

    @Resource
    SensitiveFilter sensitiveFilter;

    @Override
    public int findDiscussPostRows(int userId) {
        return discussPostDao.selectDiscussPostRows(userId);
    }

    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit,int orderMode) {
        return discussPostDao.selectDiscussPost(userId, offset, limit,orderMode);
    }

    @Override
    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        // 转义html标签
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        // 过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        // 插入数据库
        return discussPostDao.insert(post);
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public DiscussPost queryById(Integer id) {
        return this.discussPostDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<DiscussPost> queryAllByLimit(int offset, int limit) {
        return this.discussPostDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param discussPost 实例对象
     * @return 实例对象
     */
    @Override
    public DiscussPost insert(DiscussPost discussPost) {
        this.discussPostDao.insert(discussPost);
        return discussPost;
    }

    /**
     * 修改数据
     *
     * @param discussPost 实例对象
     * @return 实例对象
     */
    @Override
    public DiscussPost update(DiscussPost discussPost) {
        this.discussPostDao.update(discussPost);
        return this.queryById(discussPost.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.discussPostDao.deleteById(id) > 0;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateCommentCount(Integer entityId, int commentCount) {
        this.discussPostDao.updateCommentCount(entityId, commentCount);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateType(int id, int type) {
        this.discussPostDao.updateType(id, type);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateStatus(int id, int status) {
        this.discussPostDao.updateStatus(id, status);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateScore(int id, double score) {
        this.discussPostDao.updateScore(id, score);
    }


}