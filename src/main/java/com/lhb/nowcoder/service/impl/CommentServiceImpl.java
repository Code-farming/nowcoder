package com.lhb.nowcoder.service.impl;

import com.lhb.nowcoder.dao.DiscussPostDao;
import com.lhb.nowcoder.entity.Comment;
import com.lhb.nowcoder.dao.CommentDao;
import com.lhb.nowcoder.service.CommentService;
import com.lhb.nowcoder.service.DiscussPostService;
import com.lhb.nowcoder.util.SensitiveFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;
import static com.lhb.nowcoder.util.NowCoderConstant.*;
/**
 * (Comment)表服务实现类
 *
 * @author LHb
 * @since 2020-05-08 17:43:18
 */
@Service("commentService")
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentDao commentDao;

    @Resource
    private SensitiveFilter sensitiveFilter;

    @Resource
    private DiscussPostService discussPostService;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Comment queryById(Integer id) {
        return this.commentDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<Comment> queryAllByLimit(int offset, int limit) {
        return this.commentDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param comment 实例对象
     * @return 实例对象
     */
    @Override
    public Comment insert(Comment comment) {
        this.commentDao.insert(comment);
        return comment;
    }

    /**
     * 修改数据
     *
     * @param comment 实例对象
     * @return 实例对象
     */
    @Override
    public Comment update(Comment comment) {
        this.commentDao.update(comment);
        return this.queryById(comment.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.commentDao.deleteById(id) > 0;
    }

    @Override
    public List<Comment> findCommentsByEntity(int entityType, Integer entityId, int offset, int limit) {
        return commentDao.findCommentsByEntity(entityType,entityId,offset,limit);
    }

    @Override
    public int findCommentCount(int entityType, Integer entityId) {
        return commentDao.findCommentCount(entityType,entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    @Override
    public int addComment(Comment comment) {
        if (comment == null ){
            throw new IllegalArgumentException("参数不能为空");
        }
        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));

        int rows = commentDao.insert(comment);

        // 更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST){
            int commentCount = commentDao.findCommentCount(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),commentCount);
        }

        return rows;

    }


}