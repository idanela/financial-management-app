package com.elazaridan.expenseservice.service;

import com.elazaridan.expenseservice.dto.ExpenseResponse;
import com.elazaridan.expenseservice.dto.ExpenseRequest;
import com.elazaridan.expenseservice.dto.events.ExpenseEvent;
import com.elazaridan.expenseservice.model.ExpenseData;
import com.elazaridan.expenseservice.repository.ExpenseRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public ResponseEntity<?> requestExpense (String userId, ExpenseRequest expenseRequest) throws JsonProcessingException {
        ExpenseEvent expenseEvent = new ExpenseEvent(userId,expenseRequest);
        Boolean hasSufficientFunds = false;
        String expenseEventJsonPayload = objectMapper.writeValueAsString(expenseEvent);

       hasSufficientFunds = (Boolean) rabbitTemplate.convertSendAndReceive("expense-budget-exchange",
                "add-expense-budget-routing-key",
                expenseEventJsonPayload);

        if(hasSufficientFunds.booleanValue())
        {
            return  ResponseEntity.ok(createExpenseEntry(expenseEvent));
        }
        else
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds");
        }

    }

    public ExpenseResponse createExpenseEntry(ExpenseEvent expenseEvent) {
        ExpenseData expenseData = createExpenseDataFromEvent(expenseEvent);
        repository.save(expenseData);
        log.info("Expense record from {} with amount {} was created",expenseData.getBeneficiary(),expenseData.getSum());

        return getExpenseResponse(expenseEvent);
    }

    public ExpenseResponse getExpenseByID(Long expenseId) {
        ExpenseData expenseData = repository.findById(expenseId).orElseThrow(() -> new NoSuchElementException("Expense with ID " + expenseId + " not found"));
        return ExpenseResponse.builder().sum(expenseData.getSum()).date(expenseData.getDate()).beneficiary(expenseData.getBeneficiary()).build();
    }

    public List<ExpenseResponse> getAllExpenses() {
        return repository.findAll().stream().
                map(expenseData->ExpenseResponse.builder().sum(expenseData.getSum()).date(expenseData.getDate()).beneficiary(expenseData.getBeneficiary()).build()).toList();
    }

    public void deleteExpense(Long expenseId) {
            repository.deleteById(expenseId);
    }

    public void updateRequest(Long expenseId, ExpenseRequest expenseRequest) {
        Optional<ExpenseData> expenseDataOptional = repository.findById(expenseId);
        if(expenseDataOptional.isPresent()) {
            repository.updateExpenseRecord(expenseDataOptional.get().getExpenseId(), expenseRequest.getBeneficiary(), expenseRequest.getSum(), expenseRequest.getDate());
        }
    }

    private ExpenseResponse getExpenseResponse(ExpenseEvent expenseEvent)
    {
       return ExpenseResponse.builder()
                .sum(expenseEvent.getExpenseRequest().getSum()).
               date(expenseEvent.getExpenseRequest().getDate()).
               beneficiary(expenseEvent.getExpenseRequest().getBeneficiary())
               .build();
    }

    private ExpenseData createExpenseDataFromEvent(ExpenseEvent expenseEvent)
    {
        return  ExpenseData.builder().
            sum(expenseEvent.getExpenseRequest().getSum()).
            date(expenseEvent.getExpenseRequest().getDate())
            .beneficiary(expenseEvent.getExpenseRequest().getBeneficiary())
            .userId(expenseEvent.getUserId()).build();
    }
}
