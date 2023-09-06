package com.elazaridan.userservice.dto.Income;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class IncomePutRequest {
    long incomeId;
    private String source;
    private double amount;
    private LocalDate date;

    public IncomePutRequest (long incomeId,IncomeRequest incomeRequest)
    {
        this.incomeId = incomeId;
        this.amount =  incomeRequest.getAmount();
        this.source = incomeRequest.getSource();
        this.date = incomeRequest.getDate();
    }
}
