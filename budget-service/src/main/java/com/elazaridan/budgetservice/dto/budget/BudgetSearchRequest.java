package com.elazaridan.budgetservice.dto.budget;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BudgetSearchRequest {
    @NotNull(message = "month can not be null")
    @Min(value = 1, message = "month should not be less than 1")
    @Max(value = 12, message = "month should not be greater than 12")
    private Integer month;

    @NotNull(message = "year can not be null")
    @PositiveOrZero(message = "Year should be positive")
    private Integer year;

    @NotNull(message = "user ID must be provided" )
    private String userId;

}
