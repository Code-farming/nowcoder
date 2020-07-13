package com.lhb.nowcoder;

import com.lhb.nowcoder.dao.DiscussPostDao;
import com.lhb.nowcoder.dao.elasticsearch.DiscussPostRepository;
import com.lhb.nowcoder.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NowcoderApplication.class)
public class ElasticsearchTest {
    @Resource
    private DiscussPostDao discussPostDao;

    @Resource
    private DiscussPostRepository discussPostRepository;

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void testInsert() {
        discussPostRepository.save(discussPostDao.queryById(241));
        discussPostRepository.save(discussPostDao.queryById(242));
        discussPostRepository.save(discussPostDao.queryById(243));
    }

    @Test
    public void testInsertList() {
        discussPostRepository.saveAll(discussPostDao.selectDiscussPost(101, 0, 100));
        discussPostRepository.saveAll(discussPostDao.selectDiscussPost(102, 0, 100));
        discussPostRepository.saveAll(discussPostDao.selectDiscussPost(103, 0, 100));
        discussPostRepository.saveAll(discussPostDao.selectDiscussPost(111, 0, 100));
        discussPostRepository.saveAll(discussPostDao.selectDiscussPost(112, 0, 100));
        discussPostRepository.saveAll(discussPostDao.selectDiscussPost(131, 0, 100));
        discussPostRepository.saveAll(discussPostDao.selectDiscussPost(132, 0, 100));
        discussPostRepository.saveAll(discussPostDao.selectDiscussPost(133, 0, 100));
        discussPostRepository.saveAll(discussPostDao.selectDiscussPost(134, 0, 100));
    }

    @Test
    public void testUpdate() {
        DiscussPost discussPost = discussPostDao.queryById(231);
        discussPost.setContent("我是新人,使劲灌水");
        discussPostRepository.save(discussPost);
    }

    @Test
    public void testDelete() {
        discussPostRepository.deleteById(231);
    }

    @Test
    public void testSearch() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")

                ).build();
        Page<DiscussPost> page = discussPostRepository.search(searchQuery);
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost discussPost : page) {
            System.out.println(discussPost);
        }
    }

    @Test
    public void testSearchByTemplate() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")

                ).build();
        Page<DiscussPost> page = elasticsearchTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                SearchHits hits = searchResponse.getHits();
                if (hits.getTotalHits() <= 0) {
                    return null;
                }
                List<DiscussPost> list = new ArrayList<>();
                for (SearchHit hit : hits) {
                    DiscussPost discussPost = new DiscussPost();
                    String id = hit.getSourceAsMap().get("id").toString();
                    discussPost.setId(Integer.valueOf(id));
                    String userId = hit.getSourceAsMap().get("userId").toString();
                    discussPost.setUserId(Integer.valueOf(userId));
                    String title = hit.getSourceAsMap().get("title").toString();
                    discussPost.setTitle(title);
                    String content = hit.getSourceAsMap().get("content").toString();
                    discussPost.setContent(content);
                    String status = hit.getSourceAsMap().get("status").toString();
                    discussPost.setStatus(Integer.valueOf(status));
                    String createTime = hit.getSourceAsMap().get("createTime").toString();
                    discussPost.setCreateTime(new Date(Long.valueOf(createTime)));
                    String commentCount = hit.getSourceAsMap().get("commentCount").toString();
                    discussPost.setCommentCount(Integer.valueOf(commentCount));
                    String type = hit.getSourceAsMap().get("type").toString();
                    discussPost.setType(Integer.valueOf(type));
                    String score = hit.getSourceAsMap().get("score").toString();
                    discussPost.setScore(Double.valueOf(score));

                    // 处理高亮显示后的结果
                    HighlightField titleField = hit.getHighlightFields().get("title");
                    if (titleField != null) {
                        discussPost.setTitle(titleField.getFragments()[0].toString());
                    }
                    HighlightField contentField = hit.getHighlightFields().get("content");
                    if (contentField != null) {
                        discussPost.setContent(contentField.getFragments()[0].toString());
                    }

                    list.add(discussPost);
                }
                return new AggregatedPageImpl(list, pageable, hits.getTotalHits(),
                        searchResponse.getAggregations(), searchResponse.getScrollId(), hits.getMaxScore());
            }

            @Override
            public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
                return null;
            }
        });
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost discussPost : page) {
            System.out.println(discussPost);
        }
    }
}
