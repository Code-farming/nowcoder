package com.lhb.nowcoder.controller.advice;

import com.lhb.nowcoder.util.NowCoderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 配置全局异常
 */
@ControllerAdvice(annotations = Controller.class)
@Slf4j
public class ExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public void handleException(Exception e, HttpServletRequest request,HttpServletResponse response) throws IOException {
        log.error("服务器发生异常"+e.getMessage());

        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            log.error(stackTraceElement.toString());
        }

        // 1.获取异步请求的标志
        String xRequestedWith = request.getHeader("x-requested-with");
        if (xRequestedWith.equals("XMLHttpRequest")){
            // 1.1 如果是异步请求
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(NowCoderUtil.getJsonString(1,"服务器异常"));
        }else {
            // 1.2 如果不是异步请求,直接跳转到错误页面
            response.sendRedirect(request.getContextPath()+"/error");
        }
    }
}
