package com.frankcooper.fanout.send;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FanoutSender {
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void send(String name){
        String str = "hello,"+name;
        amqpTemplate.convertAndSend("amq.fanout","",str);
    }
}
