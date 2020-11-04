package com.kakuiwong.chat.nettyserver.nettyhandler;

import com.kakuiwong.chat.model.enums.WebSocketMessageTypeEnum;
import com.kakuiwong.chat.util.NettyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
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

    @Value("${netty.server.recipientIdLength}")
    private Integer recipientIdLength;

    //接收的message格式: |int:传输类型|long:接收者id|内容:String或者文件二进制|
    //响应的message格式: |int:传输类型|long:发送者id|内容:String或者文件二进制|
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame frame) throws Exception {
        ByteBuf content = frame.content();
        String senderId = NettyUtil.getUserIdByChannelId(ctx.channel().id().asLongText());
        //获取消息类型
        int type = content.readInt();
        String recipientId = null;
        switch (WebSocketMessageTypeEnum.getByType(type)) {
            case PING:
                ctx.writeAndFlush(WebSocketMessageTypeEnum.PONG.getType());
                break;
            case TEXT_MESSAGE:
                //获取接收者id
                recipientId = content.readBytes(recipientIdLength).toString(Charset.defaultCharset());
                //获取发送的信息
                String recipientMessage = content.toString(Charset.defaultCharset());
                NettyUtil.sendTextByUserId(senderId, recipientId, recipientMessage, ctx);
                break;
            case IMG_MESSAGE:
                recipientId = content.readBytes(recipientIdLength).toString(Charset.defaultCharset());
                NettyUtil.sendBinaryByUserId(senderId, recipientId, content, ctx, true);
                break;
            case FILE_MESSAGE:
                recipientId = content.readBytes(recipientIdLength).toString(Charset.defaultCharset());
                NettyUtil.sendBinaryByUserId(senderId, recipientId, content, ctx, false);
                break;
        }
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
