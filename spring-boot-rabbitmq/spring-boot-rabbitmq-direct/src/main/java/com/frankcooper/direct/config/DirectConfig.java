package com.frankcooper.direct.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Date 2020/5/10
 * @Author Frank Cooper
 * @Description
 */
@Configuration
public class DirectConfig {
    @Bean
    public Queue helloZyhQueue(){
        return new Queue("direct-queue");
    }
}
