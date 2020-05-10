package com.frankcooper.topic.send;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TopicSender {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void send(){
        //交换机名字，匹配符，内容
        amqpTemplate.convertAndSend("amq.topic","topic-queue2.hahaha","hello");
    }
}
