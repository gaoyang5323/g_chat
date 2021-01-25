package com.kakuiwong.config;

import com.kakuiwong.constant.XConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
@Configuration
public class RabbitMmqConfig {

    @Bean
    FanoutExchange websocketFanoutExchange() {
        return new FanoutExchange(XConstant.WEBSOCKET_FANOUT_EXCHANGE);
    }

    @Bean
    public Queue websocketQueue() {
        return new Queue(XConstant.WEBSOCKET_QUEUE);
    }


    @Bean
    public Binding websocketBinding() {
        return BindingBuilder.bind(websocketQueue()).to(websocketFanoutExchange());
    }
}
