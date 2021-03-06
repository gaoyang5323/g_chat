package com.kakuiwong.chatserver.nettyserver.nettyhandler;

import com.kakuiwong.chatserver.util.NettyUtil;
import com.kakuiwong.constant.XConstant;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Component;

/**
 * @author: gaoyang
 * @Description:
 */
@ChannelHandler.Sharable
@Component
public class TextWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
        ctx.writeAndFlush(new TextWebSocketFrame(XConstant.WEBSOCKET_TEXT_ERROR));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        NettyUtil.decreaseOnlineCount();
    }
}
