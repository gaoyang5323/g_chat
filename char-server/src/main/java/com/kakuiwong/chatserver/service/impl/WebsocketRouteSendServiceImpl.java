package com.kakuiwong.chatserver.service.impl;

import com.kakuiwong.chatserver.service.WebsocketRouteSendService;
import com.kakuiwong.chatserver.util.NettyUtil;
import com.kakuiwong.model.enums.WebSocketMessageTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
@Service
public class WebsocketRouteSendServiceImpl implements WebsocketRouteSendService {

    @Value("${netty.server.recipientIdLength}")
    private Integer recipientIdLength;

    //接受: |int:传输类型|long:接收者id|内容|
    //发送: |int:传输类型|long:发送者id|内容|
    @Override
    public void BinaryHandle(ChannelHandlerContext ctx, BinaryWebSocketFrame frame) {
        ByteBuf content = frame.content();
        int type = content.readInt();
        switch (WebSocketMessageTypeEnum.getByType(type)) {
            case PING:
                ctx.writeAndFlush(WebSocketMessageTypeEnum.PONG.getType());
                break;
            case TEXT_MESSAGE:
            case IMG_MESSAGE:
            case FILE_MESSAGE:
                sendToRecipient(content, ctx, WebSocketMessageTypeEnum.getByType(type));
                break;
        }
    }

    private String getRecipientUserId(ByteBuf content) {
        return content.readBytes(recipientIdLength).toString(Charset.defaultCharset());
    }

    private void sendToRecipient(ByteBuf content, ChannelHandlerContext ctx, WebSocketMessageTypeEnum type) {
        String senderUserId = NettyUtil.getUserIdByChannelId(ctx.channel().id().asLongText());
        String recipientUserId = getRecipientUserId(content);
        Channel recipientChannel = NettyUtil.getChannelByUserId(recipientUserId);

        if (recipientChannel != null && recipientChannel.isActive()) {
            ByteBuf resultbuffer = Unpooled.buffer();
            resultbuffer.writeInt(type.getType());
            resultbuffer.writeBytes(senderUserId.getBytes());
            switch (type) {
                case TEXT_MESSAGE:
                    String message = content.toString(Charset.defaultCharset());
                    if (message == null || message.trim().equals("")) {
                        return;
                    }
                    resultbuffer.writeBytes(message.getBytes());
                    break;
                case IMG_MESSAGE:
                case FILE_MESSAGE:
                    resultbuffer.writeBytes(content);
                    break;
            }
            recipientChannel.writeAndFlush(new BinaryWebSocketFrame(resultbuffer));
            return;
        }
        //根据redis获取分片的服务信息,然后进行发送mq信息
    }
}
