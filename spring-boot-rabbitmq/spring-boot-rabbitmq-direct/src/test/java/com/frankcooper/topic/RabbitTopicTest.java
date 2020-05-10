package com.frankcooper.topic;

import com.frankcooper.topic.send.TopicSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Date 2020/5/10
 * @Author Frank Cooper
 * @Description
 */
@SpringBootTest
public class RabbitTopicTest {
    @Autowired
    private TopicSender topicSender;

    @Test
    void contextLoads() {
    }

    @Test
    void RabbitTest() {
        topicSender.send();
    }
}
