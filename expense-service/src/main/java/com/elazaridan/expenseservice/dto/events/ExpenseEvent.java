package com.elazaridan.expenseservice.dto.events;

import com.elazaridan.expenseservice.dto.ExpenseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class ExpenseEvent implements Serializable {

    private String userId;
    private ExpenseRequest expenseRequest;
}
