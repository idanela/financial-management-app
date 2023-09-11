package com.elazaridan.expenseservice.repository;

import com.elazaridan.expenseservice.model.ExpenseData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseData,Long> {

    @Transactional
    @Modifying
    @Query("UPDATE ExpenseData i " +
            "SET i.beneficiary = COALESCE(:beneficiary, i.beneficiary), " +
            "i.sum = COALESCE(:sum, i.sum), " +
            "i.date = COALESCE(:date, i.date) " +
            "WHERE i.expenseId = :expenseId")
    void updateExpenseRecord(Long expenseId, String beneficiary, Double sum, LocalDate date);
}
