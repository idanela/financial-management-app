package com.elazaridan.budgetservice.repository;

import com.elazaridan.budgetservice.dto.budget.BudgetRequest;
import com.elazaridan.budgetservice.model.BudgetData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BudgetRepository extends JpaRepository<BudgetData,Long> {
    BudgetData findBudgetDataByMonthAndYearAndUserId(int month,int year,String userId);
    @Transactional
    void deleteByMonthAndYearAndUserId(int month,int year,String userId);


    @Transactional
    @Modifying
    @Query("UPDATE BudgetData b " +
            "SET b.sum = COALESCE(:#{#request.sum}, b.sum) " +
            "WHERE b.month = :#{#request.month} AND b.year = :#{#request.year} AND b.userId = :#{#request.userId}")
    void updateBudgetRecord(@Param("request") BudgetRequest request);

    @Transactional
    @Modifying
    @Query("UPDATE BudgetData b " +
            "SET b.sum = b.sum + :#{#budgetRequest.sum} " +
            "WHERE b.userId = :#{#budgetRequest.userId} " +
            "AND b.year = :#{#budgetRequest.year} " +
            "AND b.month = :#{#budgetRequest.month}")
    void addBudgetSum(@Param("budgetRequest") BudgetRequest budgetRequest);


    @Transactional
    @Modifying
    @Query("UPDATE BudgetData b " +
            "SET b.moneySpent = b.moneySpent + :#{#budgetRequest.moneySpent} " +
            "WHERE b.userId = :#{#budgetRequest.userId} " +
            "AND b.year = :#{#budgetRequest.year} " +
            "AND b.month = :#{#budgetRequest.month}")
            void addExpenseToBudget(BudgetRequest budgetRequest);
}
