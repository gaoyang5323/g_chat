package com.kakuiwong.chatserver.service;

import com.rabbitmq.client.Channel;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
public interface MqReceiveServce {

    void websocketReceive(String message,Channel channel);
}
