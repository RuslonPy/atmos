package com.order_service.micro.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableRabbit
public class RabbitConfig {

    @Value("${apelsin.rabbitmq.host}")
    private String host;
    @Value("${apelsin.rabbitmq.port}")
    private Integer port;
    @Value("${apelsin.rabbitmq.username}")
    private String username;
    @Value("${apelsin.rabbitmq.password}")
    private String password;


    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange("order.exchange");
    }

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue("order.created.queue");
    }

    @Bean
    public Queue orderDeletedQueue() {
        return new Queue("order.deleted.queue");
    }

    @Bean
    public Queue orderUpdatedQueue() {
        return new Queue("order.updated.queue");
    }

    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(orderExchange).with("order.created");
    }

    @Bean
    public Binding orderDeletedBinding(Queue orderDeletedQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderDeletedQueue).to(orderExchange).with("order.deleted");
    }

    @Bean
    public Binding orderUpdatedBinding(Queue orderUpdatedQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderUpdatedQueue).to(orderExchange).with("order.updated");
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setRequestedHeartBeat(15);
        connectionFactory.setConnectionTimeout(500);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}
