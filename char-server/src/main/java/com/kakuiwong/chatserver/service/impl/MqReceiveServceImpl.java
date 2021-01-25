package com.kakuiwong.chatserver.service.impl;

import com.kakuiwong.chatserver.service.MqReceiveServce;
import com.kakuiwong.chatserver.util.NettyUtil;
import com.kakuiwong.constant.XConstant;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Service;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
@Service
public class MqReceiveServceImpl implements MqReceiveServce {

    @RabbitHandler
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = XConstant.WEBSOCKET_FANOUT_EXCHANGE, durable = "true", type = ExchangeTypes.FANOUT),
            value = @Queue(value = XConstant.WEBSOCKET_QUEUE, durable = "true")
    ))
    @Override
    public void websocketReceive(String message, Channel channel) {
        //TODO 根据mq消息进行转发
    }
}
