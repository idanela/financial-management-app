package com.elazaridan.budgetservice.dto.expense;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ExpenseRequest {
    private String beneficiary;
    private double sum;
    private LocalDate date;
}
