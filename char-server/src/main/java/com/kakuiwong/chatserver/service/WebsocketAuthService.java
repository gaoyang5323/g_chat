package com.kakuiwong.chatserver.service;

import com.kakuiwong.model.bo.LoginBO;
import com.kakuiwong.model.vo.XResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author: gaoyang
 * @Description:
 */
public interface WebsocketAuthService {
    boolean auth(ChannelHandlerContext ctx, FullHttpRequest request);
}
