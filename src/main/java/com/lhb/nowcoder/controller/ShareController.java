package com.lhb.nowcoder.controller;

import com.lhb.nowcoder.entity.Event;
import com.lhb.nowcoder.event.EventProducer;
import com.lhb.nowcoder.util.NowCoderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.lhb.nowcoder.util.NowCoderConstant.*;

@Controller
@Slf4j
public class ShareController {

    @Resource
    private EventProducer eventProducer;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${wk.image.storage}")
    private String wkImageStorage;


    @RequestMapping(path = "/share", method = RequestMethod.GET)
    @ResponseBody
    public String share(String htmlUrl) {
        String fileName = NowCoderUtil.generateUUID();
        // 异步生成长图
        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                .setData("htmlUrl", htmlUrl)
                .setData("fileName", fileName)
                .setData("suffix", ".png");
        eventProducer.fireMessage(event);

        // 返回访问路径
        Map<String, Object> map = new HashMap<>();
        map.put("shareUrl", domain + contextPath + "/share/image/" + fileName);

        return NowCoderUtil.getJsonString(0, null, map);

    }

    @RequestMapping(path = "/share/image/{filename}", method = RequestMethod.GET)
    public void getShareImage(@PathVariable String filename, HttpServletResponse response) {
        if (StringUtils.isBlank(filename)) {
            throw new RuntimeException("文件名不能为空!");
        }
        response.setContentType("image/png");
        try (
                FileInputStream fileInputStream = new FileInputStream(new File(wkImageStorage + "/" + filename+ ".png"));
                OutputStream outputStream = response.getOutputStream()
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, b);
            }
        } catch (Exception e) {
            log.error("获取图片失败");
        }
    }


}
