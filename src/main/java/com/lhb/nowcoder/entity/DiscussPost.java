package com.lhb.nowcoder.entity;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * (DiscussPost)实体类
 *
 * @author LHb
 * @since 2020-05-08 18:02:42
 */
@Data
@Document(indexName = "discusspost", type = "_doc", shards = 6, replicas = 3)
public class DiscussPost implements Serializable {
    private static final long serialVersionUID = -62401778972908847L;

    @Field(type = FieldType.Integer)
    private Integer id;

    @Field(type = FieldType.Integer)
    private Integer userId;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    /**
     * 0-普通; 1-置顶;
     */
    @Field(type = FieldType.Integer)
    private Integer type;

    /**
     * 0-正常; 1-精华; 2-拉黑;
     */
    @Field(type = FieldType.Integer)
    private Integer status;

    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Integer)
    private Integer commentCount;

    @Field(type = FieldType.Double)
    private double score;
}