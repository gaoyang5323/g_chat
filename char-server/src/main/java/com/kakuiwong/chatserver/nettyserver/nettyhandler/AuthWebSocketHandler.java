package com.kakuiwong.chatserver.nettyserver.nettyhandler;

import com.kakuiwong.chatserver.service.WebsocketAuthService;
import com.kakuiwong.chatserver.util.NettyUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: gaoyang
 * @Description:
 */
@ChannelHandler.Sharable
@Component
public class AuthWebSocketHandler extends ChannelInboundHandlerAdapter {

    @Value("${netty.server.uri}")
    private String nettyUri;
    @Value("${netty.server.maxFramePayloadLength}")
    private Integer maxFramePayloadLength;
    @Autowired
    WebsocketAuthService websocketAuthService;

    private WebSocketServerHandshaker handshaker;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocket(ctx, (WebSocketFrame) msg);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri().split("\\?")[0];
        if (!request.decoderResult().isSuccess() ||
                !nettyUri.equals(uri) ||
                !"websocket".equals(request.headers().get("Upgrade"))) {
            sendErrorHttp(ctx);
            return;
        }
        if (!websocketAuthService.auth(ctx, request)) {
            sendErrorHttp(ctx);
            return;
        }
        WebSocketServerHandshakerFactory handshakerFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:9898/chat", null, true, maxFramePayloadLength);
        handshaker = handshakerFactory.newHandshaker(request);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), request);
        }
    }

    private void sendErrorHttp(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST)).
                addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        NettyUtil.userOffline(ctx.channel().id().asLongText());
    }

    private void handleWebSocket(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 判断是否关闭链路命令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否Ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 判断是否Pong消息
        if (frame instanceof PongWebSocketFrame) {
            ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        ctx.fireChannelRead(frame.retain());
    }
}
