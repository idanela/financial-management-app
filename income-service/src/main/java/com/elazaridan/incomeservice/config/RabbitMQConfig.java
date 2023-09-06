package com.elazaridan.incomeservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "income-exchange";


    public static final String QUEUE_NAME = "income-queue";
    public static final String ROUTING_KEY = "income-routing-key";

    public static final String ROUTING_RETRIEVE_KEY = "income-retrieve-routing-key";
    public static final String QUEUE_RETRIEVE_INCOME_NAME = "income-retrieve-queue";

    public static final String INCOME_QUEUE_BY_ID_NAME = "income-by-id-queue";
    public static final String INCOME_QUEUE_BY_ID_KEY = "income-by-id-routing-key";


    public static final String ROUTING_INCOME_DELETE_KEY = "income-delete-routing-key";
    public static final String QUEUE_DELETE_INCOME_NAME = "income-delete-queue";

    public static final String ROUTING_INCOME_UPDATE_KEY = "income-update-routing-key";
    public static final String QUEUE_UPDATE_INCOME_NAME = "income-update-queue";


    @Bean
    public Queue incomeQueue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Queue incomeRetrieveQueue() {
        return new Queue(QUEUE_RETRIEVE_INCOME_NAME);
    }

    @Bean
    Queue incomeQueueById() {
        return new Queue(INCOME_QUEUE_BY_ID_NAME);
    }

    @Bean
    public Queue incomeDeleteQueue() {
        return new Queue(QUEUE_DELETE_INCOME_NAME);
    }

    @Bean
    public Queue incomeUpdateQueue() {
        return new Queue(QUEUE_UPDATE_INCOME_NAME);
    }

    @Bean
    public DirectExchange incomeExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(incomeQueue()).to(incomeExchange()).with(ROUTING_KEY);
    }

    @Bean
    public Binding bindingForRetrieve() {
        return BindingBuilder.bind(incomeRetrieveQueue()).to(incomeExchange()).with(ROUTING_RETRIEVE_KEY);
    }
    @Bean
    public Binding bindingForRetrieveByID() {
        return BindingBuilder.bind(incomeQueueById()).to(incomeExchange()).with(INCOME_QUEUE_BY_ID_KEY);
    }

    @Bean
    public Binding bindingForDelete() {
        return BindingBuilder.bind(incomeDeleteQueue()).to(incomeExchange()).with(ROUTING_INCOME_DELETE_KEY);
    }

    @Bean
    public Binding bindingForUpdate() {
        return BindingBuilder.bind(incomeUpdateQueue()).to(incomeExchange()).with(ROUTING_INCOME_UPDATE_KEY);
    }
}
