package com.kakuiwong.chatserver.nettyserver.nettyhandler;

import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;

/**
 * @author: gaoyang
 * @Description:
 */
@ChannelHandler.Sharable
@Component
public class HeartBeatWebSocketHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.fireChannelRead(msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent evnet = (IdleStateEvent) evt;
            switch (evnet.state()) {
                case READER_IDLE:
                    final String remoteAddress = this.parseChannelRemoteAddr(ctx.channel());
                    System.out.println("将要移除channel" + remoteAddress);
                    ctx.writeAndFlush(new TextWebSocketFrame("失去心跳,将断开连接")).addListener(ChannelFutureListener.CLOSE);
                    break;
                case WRITER_IDLE:
                    break;
                case ALL_IDLE:
                    break;
                default:
                    ctx.fireUserEventTriggered(evt);
            }
        }
    }

    public static String parseChannelRemoteAddr(final Channel channel) {
        if (null == channel) {
            return "";
        }
        SocketAddress remote = channel.remoteAddress();
        final String addr = remote != null ? remote.toString() : "";
        if (addr.length() > 0) {
            int index = addr.lastIndexOf("/");
            if (index >= 0) {
                return addr.substring(index + 1);
            }
            return addr;
        }
        return "";
    }
}
