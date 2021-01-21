package com.kakuiwong.chatserver.httpserver.httpconfig.intercepter;

import com.alibaba.fastjson.JSON;
import com.kakuiwong.chatserver.model.annotation.AnonymousLogin;
import com.kakuiwong.chatserver.model.enums.HttpResultcodeEnum;
import com.kakuiwong.chatserver.model.vo.LoginResultVO;
import com.kakuiwong.chatserver.model.vo.XResult;
import com.kakuiwong.chatserver.service.impl.WebsocketAuthServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class AppAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerLogin = (HandlerMethod) handler;
            if (handlerLogin.getMethod().getAnnotation(AnonymousLogin.class) != null
                    || handlerLogin.getMethod().getDeclaringClass().getAnnotation(AnonymousLogin.class) != null) {
                return true;
            }
        }
        String result = checkAuth(request);
        if (result != null) {
            print(response, XResult.failed(HttpResultcodeEnum.NOT_AUTH.getCode(), result));
            return false;
        }
        return true;
    }

    //验签方式Header: userId,timestamp,sign;sign=Md5(token+timestamp)
    private String checkAuth(HttpServletRequest request) {
        String timestamp = request.getHeader("timestamp");
        String userId = request.getHeader("userId");
        String sign = request.getHeader("sign");
        //验证必传参数
        if (StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(userId) || StringUtils.isEmpty(sign)) {
            return "缺少参数";
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime localDateTime = null;
        //验证时间戳是否过期(1min内)
        try {
            localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(timestamp)), ZoneId.of("Asia/Shanghai"));
            if (localDateTime.isAfter(now)) {
                return "请求时间超过服务器当前时间";
            }
            if (localDateTime.isBefore(now.minusMinutes(1))) {
                return "请求时间过期";
            }
        } catch (Exception e) {
            return "时间参数转换失败";
        }
        //验证token
        LoginResultVO loginResultVO = WebsocketAuthServiceImpl.userIdTokenMap.get(userId);
        if (loginResultVO == null) {
            return "不存在该账户";
        }
        String realSign = DigestUtils.md5DigestAsHex((loginResultVO.getToken() + timestamp).getBytes());
        if (!sign.equals(realSign)) {
            return "验签失败";
        }
        return null;
    }


    private void print(HttpServletResponse response, XResult result) throws IOException {
        PrintWriter writer = response.getWriter();
        writer.print(JSON.toJSONString(result));
        writer.close();
    }
}
