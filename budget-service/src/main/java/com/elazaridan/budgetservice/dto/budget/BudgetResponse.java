package com.elazaridan.budgetservice.dto.budget;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BudgetResponse {
    private int month;
    private int year;
    private double sum;
}
