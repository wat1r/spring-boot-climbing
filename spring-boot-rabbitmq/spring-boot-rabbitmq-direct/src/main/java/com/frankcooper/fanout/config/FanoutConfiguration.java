package com.frankcooper.fanout.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FanoutConfiguration {
    @Bean(name = "fanout-queue1")
    public Queue getQueue01(){
        return new Queue("fanout-queue1");
    }

    @Bean(name = "fanout-queue2")
    public Queue getQueue02(){
        return new Queue("fanout-queue2");
    }

    @Bean()
    public FanoutExchange getChange(){
        return new FanoutExchange("amq.fanout");
    }

    @Bean
    public Binding binding01(@Qualifier("fanout-queue1") Queue queue01, FanoutExchange fanoutExchange){
        return BindingBuilder.bind(queue01).to(fanoutExchange);
    }

    @Bean
    public Binding binding02(@Qualifier("fanout-queue2") Queue queue02, FanoutExchange fanoutExchange){
        return BindingBuilder.bind(queue02).to(fanoutExchange);
    }
}
