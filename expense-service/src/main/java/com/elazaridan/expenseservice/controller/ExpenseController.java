package com.elazaridan.expenseservice.controller;

import com.elazaridan.expenseservice.Exceptions.InternalServerErrorException;
import com.elazaridan.expenseservice.dto.ExpenseResponse;
import com.elazaridan.expenseservice.dto.ExpenseRequest;
import com.elazaridan.expenseservice.service.ExpenseService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/expense")
@AllArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse createExpense(@RequestBody ExpenseRequest creationRequest)
    {
       return expenseService.createExpenseEntry(creationRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ExpenseResponse> getExpenses()
    {
        return expenseService.getAllExpenses();
    }

    @GetMapping("{expenseId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getExpense(@PathVariable Long expenseId)
    {
        try {
            return ResponseEntity.ok(expenseService.getExpenseByID(expenseId));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("{expenseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExpense(@PathVariable Long expenseId)
    {
        try {
            expenseService.deleteExpense(expenseId);
        }
        catch (Exception e)
        {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @PutMapping("{expenseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateExpense(@PathVariable Long expenseId, @RequestBody ExpenseRequest expenseRequest)
    {
        try {
            expenseService.updateRequest(expenseId,expenseRequest);
        }
        catch (Exception e)
        {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
