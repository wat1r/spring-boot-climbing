package com.frankcooper.singlePoint;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 邮件消息发出者
 * @Author: zhouhui
 * @Version: V1.0
 * @Date: 2019/4/27 20:09
 */
@Slf4j
@RestController
public class EmailMQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static String EXCHANGE = "email-exchange";

    private static String KEY = "email.*";

    @GetMapping("/sendMQEmail")
    public void sendMQEmail() {
        log.info("email start send...");
        User user = new User();
        user.setName("jack");
        user.setAge(18);
        user.setSex(true);
        log.info("发送邮件：{}", user);
        rabbitTemplate.convertAndSend(EXCHANGE, KEY, JSONObject.toJSONString(user));
        log.info("email end send...");
    }

}
