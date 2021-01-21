package com.kakuiwong.chatserver.nettyserver.nettyhandler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.springframework.stereotype.Component;

/**
 * @author: gaoyang
 * @Description:
 */
@ChannelHandler.Sharable
@Component
public class TailWebSocketHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        while (ReferenceCountUtil.refCnt(msg) > 0) {
            ReferenceCountUtil.release(msg);
        }
    }
}
