package com.kakuiwong.chatserver.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

/**
 * @author: gaoyang
 * @Description:
 */
public interface WebsocketRouteSendService {
    void BinaryHandle(ChannelHandlerContext ctx, BinaryWebSocketFrame frame);
}
