package com.kakuiwong.chatserver.service.impl;

import com.kakuiwong.chatserver.service.WebsocketAuthService;
import com.kakuiwong.chatserver.util.NettyUtil;
import com.kakuiwong.constant.XConstant;
import com.kakuiwong.model.po.XUser;
import com.kakuiwong.service.UserChannelService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author: gaoyang
 * @Description:
 */
@Service
public class WebsocketAuthServiceImpl implements WebsocketAuthService {

    @Autowired
    UserChannelService userChannelService;

    @Override
    public boolean auth(ChannelHandlerContext ctx, FullHttpRequest request) {
        String token = getTokenByRequest(request);
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        XUser user = userChannelService.getUserByToken(token);
        if (user == null) {
            return false;
        }
        NettyUtil.userOnline(ctx.channel(), user.getUserId(), user.isCustomerServiceUser());
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
