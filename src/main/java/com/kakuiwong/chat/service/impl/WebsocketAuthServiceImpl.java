package com.kakuiwong.chat.service.impl;

import com.kakuiwong.chat.constant.XConstant;
import com.kakuiwong.chat.model.bo.LoginBO;
import com.kakuiwong.chat.model.po.XUser;
import com.kakuiwong.chat.model.vo.LoginResultVO;
import com.kakuiwong.chat.model.vo.XResult;
import com.kakuiwong.chat.service.WebsocketAuthService;
import com.kakuiwong.chat.util.NettyUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: gaoyang
 * @Description:
 */
@Service
public class WebsocketAuthServiceImpl implements WebsocketAuthService {
    public static Map<String, XUser> tokenUserMap = new HashMap<>();
    public static Map<String, LoginResultVO> userIdTokenMap = new HashMap<>();

    static {
        XUser user1 = new XUser("01", "01", "用户1", false);
        XUser user2 = new XUser("02", "02", "用户2", false);
        XUser user3 = new XUser("03", "03", "用户3", false);
        XUser user4 = new XUser("04", "04", "用户4", false);
        XUser user5 = new XUser("05", "05", "用户5", false);
        XUser user10 = new XUser("10", "10", "客服1", true);
        XUser user20 = new XUser("20", "20", "客服2", true);
        tokenUserMap.put("01", user1);
        tokenUserMap.put("02", user2);
        tokenUserMap.put("03", user3);
        tokenUserMap.put("04", user4);
        tokenUserMap.put("05", user5);
        tokenUserMap.put("10", user10);
        tokenUserMap.put("20", user20);
        userIdTokenMap.put("01", new LoginResultVO(user1, "01"));
        userIdTokenMap.put("02", new LoginResultVO(user2, "02"));
        userIdTokenMap.put("03", new LoginResultVO(user3, "03"));
        userIdTokenMap.put("04", new LoginResultVO(user4, "04"));
        userIdTokenMap.put("05", new LoginResultVO(user5, "05"));
        userIdTokenMap.put("10", new LoginResultVO(user10, "10"));
        userIdTokenMap.put("20", new LoginResultVO(user20, "20"));
    }

    @Override
    public XResult login(LoginBO bo) {
        LoginResultVO loginResultVO = userIdTokenMap.get(bo.getUsername());
        if (loginResultVO == null) {
            return XResult.failed("没有该账号信息");
        }
        if (!bo.getPassword().equals(loginResultVO.getUser().getPassword())) {
            return XResult.failed("密码错误");
        }
        return XResult.success(loginResultVO);
    }

    @Override
    public boolean auth(ChannelHandlerContext ctx, FullHttpRequest request) {
        String token = getTokenByRequest(request);
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        XUser user = tokenUserMap.get(token);
        if (user == null) {
            return false;
        }
        NettyUtil.userOnline(ctx.channel(), user.getUsername(), user.isCustomerServiceUser());
        return true;
    }

    private String getTokenByRequest(FullHttpRequest request) {
        String[] token = new String[1];
        new QueryStringDecoder(request.uri()).
                parameters().
                entrySet().
                forEach(entry -> {
                    if (XConstant.TOKEN.equals(entry.getKey())) {
                        token[0] = (entry.getValue().get(0));
                        return;
                    }
                });
        return token[0];
    }
}
