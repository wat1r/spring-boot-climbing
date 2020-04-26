package com.frankcooper.singlePoint;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Description: RabbitMQ配置类
 * @Author: zhouhui
 * @Version: V1.0
 * @Date: 2019/4/27 17:54
 */
@Configuration
public class RabbitMQConfig {
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    // 定义转换器，将数据序列化为JSON格式传输
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
