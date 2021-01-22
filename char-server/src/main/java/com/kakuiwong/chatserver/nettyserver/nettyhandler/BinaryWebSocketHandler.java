package com.kakuiwong.chatserver.nettyserver.nettyhandler;

import com.kakuiwong.chatserver.service.WebsocketRouteSendService;
import com.kakuiwong.chatserver.util.NettyUtil;
import com.kakuiwong.model.enums.WebSocketMessageTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

/**
 * @author: gaoyang
 * @Description:
 */
@ChannelHandler.Sharable
@Component
public class BinaryWebSocketHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {

    @Autowired
    WebsocketRouteSendService websocketRouteSendService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame frame) throws Exception {
        websocketRouteSendService.BinaryHandle(ctx,frame);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // if (cause instanceof TooLongFrameException) {
        //         ctx.writeAndFlush(new TextWebSocketFrame("文件长度过大")).addListener(ChannelFutureListener.CLOSE);
        // }
        ctx.channel().close();
        NettyUtil.decreaseOnlineCount();
    }
}
