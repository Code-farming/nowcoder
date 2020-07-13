package com.lhb.nowcoder.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
/**
 * (LoginTicket)实体类
 *
 * @author LHb
 * @since 2020-05-16 00:48:13
 */
@Data
public class LoginTicket implements Serializable {
    private static final long serialVersionUID = 920366528397620578L;
    
    private Integer id;
    
    private Integer userId;
    
    private String ticket;
    /**
    * 0-有效; 1-无效;
    */
    private Integer status;
    
    private Date expired;
}