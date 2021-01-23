package com.kakuiwong.chatserver.nettyserver.nettyhandler;

import com.kakuiwong.chatserver.service.WebsocketRouteSendService;
import com.kakuiwong.chatserver.util.NettyUtil;
import com.kakuiwong.service.UserChannelService;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: gaoyang
 * @Description:
 */
@ChannelHandler.Sharable
@Component
public class BinaryWebSocketHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {

    @Value("${netty.server.url}")
    private String nettyUrl;
    @Autowired
    WebsocketRouteSendService websocketRouteSendService;
    @Autowired
    UserChannelService userChannelService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame frame) throws Exception {
        websocketRouteSendService.BinaryHandle(ctx, frame);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof TooLongFrameException) {
            ctx.writeAndFlush(new TextWebSocketFrame("文件长度过大")).addListener(ChannelFutureListener.CLOSE);
            return;
        }
        ctx.channel().close();

        String userId = NettyUtil.getUserIdByChannelId(ctx.channel().id().asLongText());
        if (userId != null) {
            userChannelService.offLine(userId, nettyUrl);
        }
        NettyUtil.userOffline(ctx.channel().id().asLongText());
    }
}
