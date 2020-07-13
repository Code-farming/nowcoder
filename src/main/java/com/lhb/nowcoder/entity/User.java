package com.lhb.nowcoder.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
/**
 * (User)实体类
 *
 * @author LHb
 * @since 2020-05-08 18:04:19
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 881326661035344205L;
    
    private Integer id;
    
    private String username;
    
    private String password;
    
    private String salt;
    
    private String email;
    /**
    * 0-普通用户; 1-超级管理员; 2-版主;
    */
    private Integer type;
    /**
    * 0-未激活; 1-已激活;
    */
    private Integer status;
    
    private String activationCode;
    
    private String headerUrl;
    
    private Date createTime;
}