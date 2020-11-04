package com.kakuiwong.chat.util;

import com.kakuiwong.chat.model.enums.WebSocketMessageTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * @author: gaoyang
 * @Description: 存储一些用户id与连接对应关系, 粗暴的实现线程安全
 */
public final class NettyUtil {
    //所有在线人数统计
    private static final LongAdder ONLINE_COUNT = new LongAdder();
    //所有用户id对应的连接id
    private static final Map<String, String> ALL_USERID_CHANNELID = new ConcurrentHashMap();
    //所有连接id对应的用户id
    private static final Map<String, String> ALL_CHANNELID_USERID = new ConcurrentHashMap();
    //所有用户id对应的连接
    private static final Map<String, Channel> ALL_USERID_CHANNEL = new ConcurrentHashMap();
    //客服用户列表(value待定)
    private static final Map<String, String> CUSTOMER_SERVICE_USERID = new ConcurrentHashMap();
    //普通用户id对应的客服id
    private static final Map<String, String> USERID_CUSTOMER_SERVICE_USERID = new ConcurrentHashMap();
    //客服用户id对应的接待用户列表
    private static Map<String, List<String>> CUSTOMER_SERVICE_USERIDLIST = new LinkedHashMap<>();

    //发送文本信息
    public static void sendTextByUserId(String senderId, String recipientId, String message, ChannelHandlerContext ctx) {
        if (StringUtils.isEmpty(message)) {
            return;
        }
        ByteBuf resultbuffer = Unpooled.buffer();
        Channel channel = ALL_USERID_CHANNEL.get(recipientId);
        if (sendOffLine(ctx, resultbuffer, channel)) {
            return;
        }
        resultbuffer.writeInt(WebSocketMessageTypeEnum.TEXT_MESSAGE.getType());
        resultbuffer.writeBytes(senderId.getBytes());
        resultbuffer.writeBytes(message.getBytes());
        channel.writeAndFlush(new BinaryWebSocketFrame(resultbuffer));
    }

    //发送二进制信息
    public static void sendBinaryByUserId(String senderId, String recipientId, ByteBuf content, ChannelHandlerContext ctx, boolean isImage) {
        if (content.capacity() < 1) {
            return;
        }
        ByteBuf resultbuffer = Unpooled.buffer();
        Channel channel = ALL_USERID_CHANNEL.get(recipientId);
        if (sendOffLine(ctx, resultbuffer, channel)) {
            return;
        }
        if (isImage) {
            resultbuffer.writeInt(WebSocketMessageTypeEnum.IMG_MESSAGE.getType());
        } else {
            resultbuffer.writeInt(WebSocketMessageTypeEnum.FILE_MESSAGE.getType());
        }
        resultbuffer.writeBytes(senderId.getBytes());
        resultbuffer.writeBytes(content);
        channel.writeAndFlush(new BinaryWebSocketFrame(resultbuffer));
    }

    private static boolean sendOffLine(ChannelHandlerContext ctx, ByteBuf Resultbuffer, Channel channel) {
        if (channel != null && channel.isActive()) {
            return false;
        }
        Resultbuffer.writeInt(WebSocketMessageTypeEnum.OFFLINE.getType());
        ctx.writeAndFlush(new BinaryWebSocketFrame(Resultbuffer));
        return true;
    }

    //用户下线
    public static void userOffline(String channelId) {
        synchronized (NettyUtil.class) {
            if (channelId != null) {
                String userId = ALL_CHANNELID_USERID.remove(channelId);
                if (userId != null) {
                    ALL_USERID_CHANNEL.remove(userId);
                    ALL_USERID_CHANNELID.remove(userId);
                    CUSTOMER_SERVICE_USERID.remove(userId);
                    String customerServiceUserid = USERID_CUSTOMER_SERVICE_USERID.get(userId);
                    if (customerServiceUserid != null) {
                        List<String> userIds = CUSTOMER_SERVICE_USERIDLIST.get(customerServiceUserid);
                        if (userIds != null && userIds.size() > 0) {
                            userIds.remove(userId);
                        }
                    }
                }
            }
            decreaseOnlineCount();
        }
    }

    public static void userOnline(Channel channel, String userId, boolean isCustomerServiceUser) {
        String channelId = channel.id().asLongText();
        if (userId != null) {
            NettyUtil.ALL_USERID_CHANNEL.put(userId, channel);
            NettyUtil.ALL_USERID_CHANNELID.put(userId, channelId);
            NettyUtil.ALL_CHANNELID_USERID.put(channelId, userId);
            if (isCustomerServiceUser) {
                CUSTOMER_SERVICE_USERID.put(userId, "待定");
            }
        }
        increasingOnlineCount();
    }

    //用户下线统计递减在线人数
    public static void decreaseOnlineCount() {
        ONLINE_COUNT.decrement();
    }

    //用户下线统计递增在线人数
    public static void increasingOnlineCount() {
        ONLINE_COUNT.increment();
    }

    //获取统计在线人数
    public static int getOnlineCount() {
        return ONLINE_COUNT.intValue();
    }

    public static String getUserIdByChannelId(String channelId) {
        return ALL_CHANNELID_USERID.get(channelId);
    }

    //获取接待用户最少的客服id
    public static String getLeastofusercountCustomeServiceUserid() {
        synchronized (NettyUtil.class) {
            if (CUSTOMER_SERVICE_USERIDLIST.size() < 1) {
                return null;
            }
            return CUSTOMER_SERVICE_USERIDLIST.entrySet().iterator().next().getKey();
        }
    }

    //对客服接待的用户数量进行递增,并根据对应的接待人数递增排序
    public static synchronized void increasingCustomeServiceUseridCount(String customeServiceUserid, String userId) {
        synchronized (NettyUtil.class) {
            List<String> userIdList = null;
            if (!CUSTOMER_SERVICE_USERIDLIST.containsKey(customeServiceUserid)) {
                userIdList = new ArrayList<>();
                CUSTOMER_SERVICE_USERIDLIST.put(customeServiceUserid, userIdList);
            } else {
                userIdList = CUSTOMER_SERVICE_USERIDLIST.get(customeServiceUserid);
            }
            userIdList.add(userId);
            HashMap<String, List<String>> finalMap = new LinkedHashMap<>(CUSTOMER_SERVICE_USERIDLIST.size());
            CUSTOMER_SERVICE_USERIDLIST.entrySet()
                    .stream()
                    .sorted((p1, p2) -> Integer.compare(p1.getValue().size(), p2.getValue().size()))
                    .collect(Collectors.toList()).forEach(ele -> finalMap.put(ele.getKey(), ele.getValue()));
            CUSTOMER_SERVICE_USERIDLIST.clear();
            CUSTOMER_SERVICE_USERIDLIST = finalMap;
        }
    }

    //对客服接待的用户数量进行递减,并根据对应的接待人数递增排序
    public static synchronized void decreaseCustomeServiceUseridCount(String customeServiceUserid, String userId) {
        synchronized (NettyUtil.class) {
            if (!CUSTOMER_SERVICE_USERIDLIST.containsKey(customeServiceUserid)) {
                return;
            }
            List<String> userIds = CUSTOMER_SERVICE_USERIDLIST.get(customeServiceUserid);
            userIds.remove(userId);
            HashMap<String, List<String>> finalMap = new LinkedHashMap<>(CUSTOMER_SERVICE_USERIDLIST.size());
            CUSTOMER_SERVICE_USERIDLIST.entrySet()
                    .stream()
                    .sorted((p1, p2) -> Integer.compare(p1.getValue().size(), p2.getValue().size()))
                    .collect(Collectors.toList()).forEach(ele -> finalMap.put(ele.getKey(), ele.getValue()));
            CUSTOMER_SERVICE_USERIDLIST.clear();
            CUSTOMER_SERVICE_USERIDLIST = finalMap;
        }
    }
}
