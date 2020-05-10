package com.frankcooper.direct.send;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DirectSender {
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void send(String name) {
        String str = "hello," + name;
        //向名为hello的队列中塞入str
        amqpTemplate.convertAndSend("direct-queue", str);
    }

}
