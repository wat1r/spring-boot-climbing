package com.frankcooper.fanout.receive;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FanoutReceiver {
    @RabbitListener(queues = "fanout-queue1")
    public void receiver01(String str){
        System.out.println(str);
    }

    @RabbitListener(queues = "fanout-queue2")
    public void receiver02(String str){
        System.out.println(str);
    }
}
