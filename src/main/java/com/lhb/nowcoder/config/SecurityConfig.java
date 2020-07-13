package com.lhb.nowcoder.config;

import com.lhb.nowcoder.util.NowCoderUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.lhb.nowcoder.util.NowCoderConstant.*;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 忽略静态资源
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(
                "/comment/**",
                "/discuss/add",
                "/follow",
                "/unFollow",
                "/like",
                "/letter/**",
                "/notice/**",
                "/user/setting",
                "/user/upload",
                "/user/updatePassword"
        ).hasAnyAuthority(
                AUTHORITY_ADMIN, AUTHORITY_USER, AUTHORITY_MODERATOR
        ).anyRequest().permitAll()
        .and().csrf().disable();

        http.exceptionHandling()
                //未登录处理
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(NowCoderUtil.getJsonString(403,"你还没有登录"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                // 权限不足处理
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if (xRequestedWith.equals("XMLHttpRequest")){
                            // 1.1 如果是异步请求
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(NowCoderUtil.getJsonString(403,"你没有访问此功能的权限"));
                        }else {
                            // 1.2 如果不是异步请求,直接跳转到错误页面
                            response.sendRedirect(request.getContextPath()+"/denied");
                        }
                    }
                });

        // Security底层默认会拦截/logout请求,进行退出处理
        // 覆盖他默认的逻辑才能实现自己退出的代码
        http.logout().logoutUrl("/securityLogout");
    }


}
