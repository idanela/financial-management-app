package com.elazaridan.expenseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse implements Serializable {

    private String beneficiary;
    private double sum;
    private LocalDate date;
}
