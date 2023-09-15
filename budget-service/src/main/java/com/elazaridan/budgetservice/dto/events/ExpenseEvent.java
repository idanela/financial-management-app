package com.elazaridan.budgetservice.dto.events;

import com.elazaridan.budgetservice.dto.expense.ExpenseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class ExpenseEvent implements Serializable {
    private String userId;
    private ExpenseRequest expenseRequest;
}
