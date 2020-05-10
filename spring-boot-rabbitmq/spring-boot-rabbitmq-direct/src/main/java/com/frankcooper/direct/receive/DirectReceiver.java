package com.frankcooper.direct.receive;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "direct-queue")
public class DirectReceiver {
    @RabbitHandler
    public void receive(String str){
        System.out.println(str);
    }
}
