package com.lhb.nowcoder.quartz;

import com.lhb.nowcoder.entity.DiscussPost;
import com.lhb.nowcoder.service.DiscussPostService;
import com.lhb.nowcoder.service.ElasticsearchService;
import com.lhb.nowcoder.service.LikeService;
import com.lhb.nowcoder.util.RedisKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.lhb.nowcoder.util.NowCoderConstant.*;

@Slf4j
public class PostScoreRefreshJob implements Job {
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private LikeService likeService;

    @Resource
    private ElasticsearchService elasticsearchService;

    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-07-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化牛客纪元失败!", e);
        }
    }


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String redisKey = RedisKeyUtils.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);
        if (operations.size() == 0) {
            log.info("[任务取消],没有需要刷新的帖子");
            return;
        }

        log.info("[任务开始],正在找刷新帖子的分数" + operations.size());
        while (operations.size() > 0) {
            this.refresh((Integer) operations.pop());
        }
        log.info("[任务结束],帖子分数刷新完毕!");


    }

    private void refresh(Integer postId) {
        DiscussPost discussPost = discussPostService.queryById(postId);
        if (discussPost == null) {
            log.info("该帖子不存在,id=" + postId);
            return;
        }

        // 是否精华
        boolean isWonderful = (discussPost.getStatus() == 1);
        // 评论数量
        Integer commentCount = discussPost.getCommentCount();
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        // 计算权重
        double power = (isWonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        // 分数=权重+距离天数
        double score = Math.log10(Math.max(power, 1)) +
                (discussPost.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);
        // 更新帖子的分数
        discussPostService.updateScore(postId,score);
        // 同步搜索数据
        discussPost.setScore(score);
        elasticsearchService.saveDiscussPost(discussPost);

    }
}
