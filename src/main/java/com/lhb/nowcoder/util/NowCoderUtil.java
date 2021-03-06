package com.lhb.nowcoder.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class NowCoderUtil {
    // 生成随机的字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    // MD5加密
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }


    // 通过JSONObject包装向前台返回的数据
    public static  String getJsonString(int code, String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if (map!=null){
            for (String key:map.keySet()){
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static  String getJsonString(int code, String msg){
        return getJsonString(code, msg, null);
    }

    public static  String getJsonString(int code){
        return getJsonString(code, null, null);
    }





}
