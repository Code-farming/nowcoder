package com.lhb.nowcoder.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
/**
 * (Comment)实体类
 *
 * @author LHb
 * @since 2020-05-08 17:43:18
 */
@Data
public class Comment implements Serializable {
    private static final long serialVersionUID = 653190438370617498L;
    
    private Integer id;
    
    private Integer userId;
    
    private Integer entityType;
    
    private Integer entityId;
    
    private Integer targetId;
    
    private String content;
    
    private Integer status;
    
    private Date createTime;
}