package com.elazaridan.incomeservice.dto.events;

import com.elazaridan.incomeservice.dto.income.IncomeRequest;
import lombok.Builder;

import java.io.Serializable;

public class IncomeEvent implements Serializable {
    private String userId;
    private IncomeRequest incomeRequest;

    public IncomeEvent(String userId, IncomeRequest incomeRequest) {
        this.userId = userId;
        this.incomeRequest = incomeRequest;
    }

    public String getUserId() {
        return userId;
    }

    public IncomeRequest getIncomeRequest() {
        return incomeRequest;
    }
}
