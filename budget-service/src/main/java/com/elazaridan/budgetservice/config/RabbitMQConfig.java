package com.elazaridan.budgetservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "income-budget-exchange";

    public static final String QUEUE_NAME = "add-income-budget-queue";
    public static final String ROUTING_KEY = "add-income-budget-routing-key";


    public static final String EXPENSE_EXCHANGE_NAME = "expense-budget-exchange";

    public static final String EXPENSE_QUEUE_NAME = "add-expense-budget-queue";
    public static final String EXPENSE_ROUTING_KEY = "add-expense-budget-routing-key";




    @Bean
    public DirectExchange incomeBudgetExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue addIncomeQueue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Binding bindingIncome() {
        return BindingBuilder.bind(addIncomeQueue()).to(incomeBudgetExchange()).with(ROUTING_KEY);
    }
    @Bean
    public DirectExchange expenseBudgetExchange() {
        return new DirectExchange(EXPENSE_EXCHANGE_NAME);
    }

    @Bean
    public Queue addExpenseQueue() {
        return new Queue(EXPENSE_QUEUE_NAME);
    }

    @Bean
    public Binding bindingExpense() {
        return BindingBuilder.bind(addExpenseQueue()).to(expenseBudgetExchange()).with(EXPENSE_ROUTING_KEY);
    }


}
