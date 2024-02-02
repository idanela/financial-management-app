package com.elazaridan.budgetservice.service;

import com.elazaridan.budgetservice.dto.Income.IncomeRequest;
import com.elazaridan.budgetservice.dto.budget.BudgetRequest;
import com.elazaridan.budgetservice.dto.budget.BudgetSearchRequest;
import com.elazaridan.budgetservice.dto.events.ExpenseEvent;
import com.elazaridan.budgetservice.dto.events.IncomeEvent;
import com.elazaridan.budgetservice.dto.expense.ExpenseRequest;
import com.elazaridan.budgetservice.model.BudgetData;
import com.elazaridan.budgetservice.repository.BudgetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {
    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private BudgetService budgetService;

    @Test
    void CheckBudgetCreated() {
        BudgetRequest budgetRequest = BudgetRequest.builder().sum(100d).moneySpent(20d).month(12).year(2023).userId("Idan").build();
        BudgetData budgetData = BudgetData.builder().sum(100d).moneySpent(20d).month(12).year(2023).userId("Idan").build();
        when(budgetRepository.findBudgetDataByMonthAndYearAndUserId(eq(12), eq(2023), any(String.class))).thenReturn(null);
        when(budgetRepository.save(any(BudgetData.class))).thenReturn(budgetData);

       boolean created = budgetService.createBudget(budgetRequest);
        verify(budgetRepository).save(any(BudgetData.class));
        assertTrue(created);
        assertEquals(100d, budgetData.getSum());
        assertEquals(20d, budgetData.getMoneySpent());
        assertEquals(12, budgetData.getMonth());
        assertEquals(2023, budgetData.getYear());
        assertEquals("Idan", budgetData.getUserId());
    }

    @Test
    void checkBudgetWasNotCreated() {
        BudgetRequest budgetRequest = BudgetRequest.builder().sum(100d).moneySpent(20d).month(12).year(2023).userId("Idan").build();
        BudgetData budgetData = BudgetData.builder().sum(100d).moneySpent(20d).month(12).year(2023).userId("Idan").build();
        when(budgetRepository.findBudgetDataByMonthAndYearAndUserId(eq(12), eq(2023), any(String.class))).thenReturn(budgetData);
        boolean created = budgetService.createBudget(budgetRequest);
        verify(budgetRepository,never()).save(any(BudgetData.class));
        assertFalse(created);
    }
    @Test
    void checkGetALLBudgets() {
        //Arrange
        budgetService.getALLBudgets();
        //then
        verify(budgetRepository).findAll();
    }

    @Test
    void checkGetBudgetByMonthAndYear() {
       //Arrange
        BudgetData budgetData = BudgetData.builder().budgetId(1L).sum(100d).moneySpent(20d).month(12).year(2023).userId("Idan").build();
        //Act
        when(budgetRepository.findBudgetDataByMonthAndYearAndUserId(12,2023,"Idan")).thenReturn(budgetData);
        BudgetData savedBudget = budgetRepository.findBudgetDataByMonthAndYearAndUserId(12, 2023, "Idan");

        //Assert
        assertThat(savedBudget).isNotNull();
        assertThat(savedBudget).isEqualTo(budgetData);
    }

    @Test
    void checkDeleteBudget() {
        //Arrange
        BudgetSearchRequest budgetSearchRequest = BudgetSearchRequest.builder().userId("Idan").year(2023).month(12).build();
        //Act
        doNothing().when(budgetRepository).deleteByMonthAndYearAndUserId(anyInt(),anyInt(),anyString());
        budgetService.deleteBudget(budgetSearchRequest);
        //Assert
        verify(budgetRepository,times(1)).deleteByMonthAndYearAndUserId(anyInt(),anyInt(),anyString());
    }

    @Test
    void checkUpdateBudget() {
        BudgetRequest budgetRequest = BudgetRequest.builder().sum(100d).moneySpent(20d).month(12).year(2023).userId("Idan").build();
        doNothing().when(budgetRepository).updateBudgetRecord(any(BudgetRequest.class));
        // Act
        budgetService.updateBudget(budgetRequest);
        verify(budgetRepository,times(1)).updateBudgetRecord(any(BudgetRequest.class));
    }

    @Test
    void checkAddIncomeToNonExistingBudget() {
        IncomeRequest incomeRequest = IncomeRequest.builder().amount(50).date(LocalDate.now()).source("Job").build();
        IncomeEvent incomeEvent = new IncomeEvent("Idan", incomeRequest);

        when(budgetRepository.findBudgetDataByMonthAndYearAndUserId(anyInt(), anyInt(), anyString()))
                .thenReturn(null);

        when(budgetRepository.save(any(BudgetData.class))).thenReturn(new BudgetData());

        budgetService.addIncome(incomeEvent);

        verify(budgetRepository, times(1)).save(any(BudgetData.class));
    }

    @Test
    void checkAddIncomeToExisting() {
        BudgetData budgetData = BudgetData.builder().budgetId(1L).sum(100d).moneySpent(20d).month(12).year(2023).userId("Idan").build();
        IncomeRequest incomeRequest = IncomeRequest.builder().amount(50).date(LocalDate.now()).source("Job").build();
        IncomeEvent incomeEvent = new IncomeEvent("Idan", incomeRequest);

        when(budgetRepository.findBudgetDataByMonthAndYearAndUserId(anyInt(), anyInt(), anyString()))
                .thenReturn(budgetData); // Simulate that budget exists for the given month, year, and user
        budgetService.addIncome(incomeEvent);

        // Verify that the save method is called
        verify(budgetRepository, times(0)).save(any(BudgetData.class));
        verify(budgetRepository, times(1)).addBudgetSum(any(BudgetRequest.class));
    }

    @Test
    void checkAddExpenseForExistingBudget() {
        //Arrange
        BudgetData budgetData = BudgetData.builder().budgetId(1L).sum(100d).moneySpent(20d).month(12).year(2023).userId("Idan").build();
        ExpenseRequest expenseRequest = ExpenseRequest.builder().beneficiary("Office Depot").sum(10d).date(LocalDate.now()).build();
        ExpenseEvent expenseEvent = new ExpenseEvent("Idan",expenseRequest);

        when(budgetRepository.findBudgetDataByMonthAndYearAndUserId(anyInt(),anyInt(),anyString())).thenReturn(budgetData);
        doNothing().when(budgetRepository).addExpenseToBudget(any(BudgetRequest.class));

        budgetService.addExpense(expenseEvent);

        verify(budgetRepository, times(1)).addExpenseToBudget(any(BudgetRequest.class));
    }
}