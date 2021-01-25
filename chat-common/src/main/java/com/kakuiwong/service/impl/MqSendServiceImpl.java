package com.kakuiwong.service.impl;

import com.kakuiwong.service.MqSendService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
@Service
public class MqSendServiceImpl implements MqSendService {

    @Autowired
    RabbitTemplate rabbitTemplate;

}
