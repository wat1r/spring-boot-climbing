package com.frankcooper.singlePoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Description: 邮件消息接收者
 * @Author: zhouhui
 * @Version: V1.0
 * @Date: 2019/4/27 20:37
 */
@Slf4j
@Component
public class EmailMQReceiver {
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "email-queue", durable = "true"),
            exchange = @Exchange(value = "email-exchange", durable = "true", type = "topic"),
            key = "email.*"
    ))
    public void receiveMQEmail(String message) {
        log.info("email start receive...");
        log.info("接收邮件：{}", message);
        log.info("email end receive...");
    }

}
