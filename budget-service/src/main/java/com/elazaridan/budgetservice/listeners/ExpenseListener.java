package com.elazaridan.budgetservice.listeners;

import com.elazaridan.budgetservice.dto.events.ExpenseEvent;
import com.elazaridan.budgetservice.service.BudgetService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
@Component
@AllArgsConstructor
public class ExpenseListener {
    private final BudgetService budgetService;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;


    @RabbitListener(queues = "add-expense-budget-queue")
    public Boolean addExpense(String jsonExpenseEvent) throws JsonProcessingException {
        Boolean hasSufficientFunds = false;

        ExpenseEvent expenseEvent = objectMapper.readValue(jsonExpenseEvent, ExpenseEvent.class);
        hasSufficientFunds = budgetService.addExpense(expenseEvent);
        return hasSufficientFunds;
    }


}
