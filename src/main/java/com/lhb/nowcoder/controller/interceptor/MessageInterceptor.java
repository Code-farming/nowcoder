package com.lhb.nowcoder.controller.interceptor;

import com.lhb.nowcoder.entity.User;
import com.lhb.nowcoder.service.MessageService;
import com.lhb.nowcoder.util.HostHolder;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {
    @Resource
    private HostHolder hostHolder;
    @Resource
    private MessageService messageService;


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null){
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
            int noticeUnReadCount = messageService.findNoticeUnReadCount(user.getId(), null);
            modelAndView.addObject("allUnreadCount",letterUnreadCount+noticeUnReadCount);
        }
    }
}
