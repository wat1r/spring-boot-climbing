package com.frankcooper.topic.receive;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TopicReceiver {
    @RabbitListener(queues = "topic-queue1")
    public void receiver01(String str){
        System.out.println(str + " from topic-queue1");
    }

    @RabbitListener(queues = "topic-queue2")
    public void receiver02(String str){
        System.out.println(str + " from topic-queue2");
    }
}
