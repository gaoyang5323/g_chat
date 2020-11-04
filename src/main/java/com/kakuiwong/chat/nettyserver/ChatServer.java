package com.kakuiwong.chat.nettyserver;

import com.kakuiwong.chat.nettyserver.nettyhandler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author: gaoyang
 * @Description:
 */
@Component
public class ChatServer {

    private EventLoopGroup bossGroup = new NioEventLoopGroup(new DefaultThreadFactory("bossGroup", true));
    private EventLoopGroup workGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(),
            new DefaultThreadFactory("bossGroup", true));

    @Value("${netty.server.port}")
    private Integer port;
    @Autowired
    TextWebSocketHandler textWebSocketHandler;
    @Autowired
    HeartBeatWebSocketHandler heartBeatWebSocketHandler;
    @Autowired
    AuthWebSocketHandler authWebSocketHandler;
    @Autowired
    TailWebSocketHandler tailWebSocketHandler;
    @Autowired
    BinaryWebSocketHandler binaryWebSocketHandler;

    @PostConstruct
    public void start() {
        new Thread(() -> {
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workGroup)
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .handler(new LoggingHandler(LogLevel.TRACE))
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline()
                                        .addLast(new LoggingHandler(LogLevel.TRACE))
                                        // HttpRequestDecoder和HttpResponseEncoder的一个组合，针对http协议进行编解码
                                        .addLast(new HttpServerCodec())
                                        // 分块向客户端写数据，防止发送大文件时导致内存溢出， channel.write(new ChunkedFile(new File("bigFile.mkv")))
                                        .addLast(new ChunkedWriteHandler())
                                        // 将HttpMessage和HttpContents聚合到一个完成的 FullHttpRequest或FullHttpResponse中,具体是FullHttpRequest对象还是FullHttpResponse对象取决于是请求还是响应
                                        // 需要放到HttpServerCodec这个处理器后面
                                        .addLast(new HttpObjectAggregator(10240))
                                        // webSocket 数据压缩扩展，当添加这个的时候WebSocketServerProtocolHandler的第三个参数需要设置成true
                                        .addLast(new WebSocketServerCompressionHandler())
                                        // 聚合 websocket 的数据帧，因为客户端可能分段向服务器端发送数据
                                        // https://github.com/netty/netty/issues/1112 https://github.com/netty/netty/pull/1207
                                        .addLast(new WebSocketFrameAggregator(10 * 1024 * 1024))
                                        // 服务器端向外暴露的 web socket 端点，当客户端传递比较大的对象时，maxFrameSize参数的值需要调大
                                        //.addLast(new WebSocketServerProtocolHandler("/chat", null, true, 10485760))
                                        .addLast(new IdleStateHandler(60 * 10, 0, 0)) //检测链路是否读空闲)
                                        .addLast(heartBeatWebSocketHandler)
                                        .addLast(authWebSocketHandler)
                                        .addLast(textWebSocketHandler)
                                        .addLast(binaryWebSocketHandler)
                                        .addLast(tailWebSocketHandler);
                            }
                        });
                ChannelFuture channelFuture = bootstrap.bind(port).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                stop();
            } finally {
                stop();
            }
        }).start();
    }

    @PreDestroy
    public void destroy() {
        stop();
    }

    private void stop() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
