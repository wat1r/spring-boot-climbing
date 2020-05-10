package com.frankcooper.direct;

import com.frankcooper.direct.receive.DirectReceiver;
import com.frankcooper.direct.send.DirectSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Date 2020/5/10
 * @Author Frank Cooper
 * @Description
 */
@SpringBootTest
public class RabbitDirectTest {

    @Autowired
    private DirectSender directSender;
    @Autowired
    private DirectReceiver directReceiver;

    @Test
    void rabbitmqTest() {
        String msg = "direct";
        System.out.println(String.format("sender has send msg:%s", msg));
        directSender.send(msg);
    }
}
