package com.kakuiwong.chatserver.service.impl;

import com.kakuiwong.chatserver.service.WebsocketAuthService;
import com.kakuiwong.chatserver.util.NettyUtil;
import com.kakuiwong.constant.XConstant;
import com.kakuiwong.model.bo.LoginBO;
import com.kakuiwong.model.po.XUser;
import com.kakuiwong.model.vo.LoginResultVO;
import com.kakuiwong.model.vo.XResult;
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

    @Override
    public boolean auth(ChannelHandlerContext ctx, FullHttpRequest request) {
        String token = getTokenByRequest(request);
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        //从redis获取token
        XUser user = null;
        if (user == null) {
            return false;
        }
        //验证通过,记录登录的用户信息;
        //本地存储 用户id->channel
        //redis存储 用户id->服务器信息;此信息代表用户在线,并且发送的mq信道以服务器信息作为的名称
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
