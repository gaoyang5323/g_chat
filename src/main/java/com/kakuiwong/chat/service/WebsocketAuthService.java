package com.kakuiwong.chat.service;

import com.kakuiwong.chat.model.bo.LoginBO;
import com.kakuiwong.chat.model.vo.XResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author: gaoyang
 * @Description:
 */
public interface WebsocketAuthService {

    boolean auth(ChannelHandlerContext ctx, FullHttpRequest request);

    XResult login(LoginBO bo);
}
