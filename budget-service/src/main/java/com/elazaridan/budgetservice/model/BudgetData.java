package com.elazaridan.budgetservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Entity
@Component
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="Budgets")
public class BudgetData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long budgetId;
    private String userId;
    private int year;
    private int month;
    private double sum;
    private double moneySpent;
}
