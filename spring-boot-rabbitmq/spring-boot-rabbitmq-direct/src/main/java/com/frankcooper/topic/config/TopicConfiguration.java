package com.frankcooper.topic.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicConfiguration {
    @Bean(name = "topic-queue1")
    Queue getQueue01(){
        return new Queue("topic-queue1");
    }

    @Bean(name = "topic-queue2")
    Queue gwtQueue02(){
        return new Queue("topic-queue2");
    }

    @Bean
    TopicExchange getExchange(){
        return new TopicExchange("amq.topic");
    }

    @Bean
    Binding getBinding01(@Qualifier("topic-queue1") Queue queue, TopicExchange topicExchange){
        return BindingBuilder.bind(queue).to(topicExchange).with("topic-queue1.#");
    }

    @Bean
    Binding getBinding02(@Qualifier("topic-queue2") Queue queue, TopicExchange topicExchange){
        return BindingBuilder.bind(queue).to(topicExchange).with("topic-queue2.#");
    }
}
