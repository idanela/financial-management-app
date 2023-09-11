package com.elazaridan.expenseservice.service;

import com.elazaridan.expenseservice.dto.ExpenseResponse;
import com.elazaridan.expenseservice.dto.ExpenseRequest;
import com.elazaridan.expenseservice.model.ExpenseData;
import com.elazaridan.expenseservice.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository repository;
    public ExpenseResponse createExpenseEntry(ExpenseRequest creationRequest) {
        ExpenseData expenseData =ExpenseData.builder().sum(creationRequest.getSum()).date(creationRequest.getDate()).beneficiary(creationRequest.getBeneficiary()).build();
        repository.save(expenseData);
        log.info("Expense record from {} with amount {} was created",expenseData.getBeneficiary(),expenseData.getSum());

        return ExpenseResponse.builder().sum(creationRequest.getSum()).date(creationRequest.getDate()).beneficiary(creationRequest.getBeneficiary()).build();
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
        if(expenseDataOptional.isPresent())
        {
           repository.updateExpenseRecord(expenseDataOptional.get().getExpenseId(),expenseRequest.getBeneficiary(),expenseRequest.getSum(),expenseRequest.getDate());
        }
    }
}
