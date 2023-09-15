package com.elazaridan.budgetservice.listeners;

import com.elazaridan.budgetservice.dto.events.IncomeEvent;
import com.elazaridan.budgetservice.service.BudgetService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class IncomeListener {
    private final BudgetService budgetService;
    private final ObjectMapper objectMapper;


    @RabbitListener(queues = "add-income-budget-queue")
    public void addIncome(String incomeEventJson)
    {
        try {
            IncomeEvent incomeEvent = objectMapper.readValue(incomeEventJson, IncomeEvent.class);
            budgetService.addIncome(incomeEvent);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
