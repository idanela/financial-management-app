package com.elazaridan.expenseservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "expenses")
public class ExpenseData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long expenseId;
    private String beneficiary;
    private double sum;
    private LocalDate date;

}
