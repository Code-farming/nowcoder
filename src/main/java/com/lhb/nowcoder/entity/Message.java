package com.lhb.nowcoder.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
/**
 * (Message)实体类
 *
 * @author LHb
 * @since 2020-07-08 12:15:57
 */
@Data
public class Message implements Serializable {
    private static final long serialVersionUID = -89732387659397090L;
    
    private Integer id;
    
    private Integer fromId;
    
    private Integer toId;
    
    private String conversationId;
    
    private String content;
    /**
    * 0-未读;1-已读;2-删除;
    */
    private Integer status;
    
    private Date createTime;
}