package com.lhb.nowcoder.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
@Slf4j
public class WkConfig {
    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @Value("${wk.image.command}")
    private String wkImageCommand;

    @PostConstruct
    public void init(){
        File file = new File(wkImageStorage);
        if (!file.exists()){
            file.mkdir();
            log.info("创建WK图片目录:"+wkImageStorage);
        }

    }
}
