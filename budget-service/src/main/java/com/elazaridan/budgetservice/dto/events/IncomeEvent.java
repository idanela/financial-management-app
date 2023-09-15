package com.elazaridan.budgetservice.dto.events;

import com.elazaridan.budgetservice.dto.Income.IncomeRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class IncomeEvent implements Serializable {

    private String userId;
    private IncomeRequest incomeRequest;


}
