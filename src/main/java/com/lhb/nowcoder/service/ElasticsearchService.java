package com.lhb.nowcoder.service;

import com.lhb.nowcoder.entity.DiscussPost;
import org.springframework.data.domain.Page;

public interface ElasticsearchService {
    void saveDiscussPost(DiscussPost discussPost);

    void deleteDiscussPost(int id);

    Page<DiscussPost> searchDiscussPost(String keyword,int current,int limit);
}
