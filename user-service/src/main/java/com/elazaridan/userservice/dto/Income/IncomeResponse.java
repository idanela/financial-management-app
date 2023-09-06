package com.elazaridan.userservice.dto.Income;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class IncomeResponse implements Serializable {
    private String source;
    private double amount;
    private LocalDate date;
}
