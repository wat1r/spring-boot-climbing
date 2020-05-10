package com.frankcooper.fanout;

import com.frankcooper.fanout.send.FanoutSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Date 2020/5/10
 * @Author Frank Cooper
 * @Description
 */
@SpringBootTest
public class RabbitFanoutTest {
    @Autowired
    private FanoutSender fanoutSender;

    @Test
    void contextLoads() {
    }

    @Test
    void send(){
        fanoutSender.send("fanout");
    }

}
