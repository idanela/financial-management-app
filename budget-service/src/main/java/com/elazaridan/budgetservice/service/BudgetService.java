package com.elazaridan.budgetservice.service;

import com.elazaridan.budgetservice.dto.budget.BudgetRequest;
import com.elazaridan.budgetservice.dto.budget.BudgetResponse;
import com.elazaridan.budgetservice.dto.budget.BudgetSearchRequest;
import com.elazaridan.budgetservice.dto.events.ExpenseEvent;
import com.elazaridan.budgetservice.dto.events.IncomeEvent;
import com.elazaridan.budgetservice.model.BudgetData;
import com.elazaridan.budgetservice.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;
    @Value("${budget.percentage}")
    private double budgetPercentage;



    public boolean createBudget(BudgetRequest budgetRequest) {
        boolean doesEntryCreated = false;
        BudgetData budgetDataByMonthAndYear = budgetRepository.findBudgetDataByMonthAndYearAndUserId(budgetRequest.getMonth(), budgetRequest.getYear(),budgetRequest.getUserId());
        if(budgetDataByMonthAndYear==null)
        {
            doesEntryCreated = true;
            budgetRepository.save(BudgetData.builder().year(budgetRequest.getYear()).month(budgetRequest.getMonth()).sum(budgetRequest.getSum()!=null?budgetRequest.getSum():0).userId(budgetRequest.getUserId()).build());
        }
        return doesEntryCreated;
    }

    public List<BudgetData> getALLBudgets() {
        return budgetRepository.findAll();
    }

    public BudgetResponse getBudgetByMonthAndYear(BudgetSearchRequest budgetSearchRequest) {
        BudgetResponse response = null;
        BudgetData budgetData = budgetRepository.findBudgetDataByMonthAndYearAndUserId(budgetSearchRequest.getMonth(), budgetSearchRequest.getYear(), budgetSearchRequest.getUserId());
        if(budgetData!=null)
            response= BudgetResponse.builder().month(budgetData.getMonth()).year(budgetData.getYear()).sum(budgetData.getSum()).build();
        return response;
    }

    public void deleteBudget(BudgetSearchRequest budgetSearchRequest) {
        budgetRepository.deleteByMonthAndYearAndUserId(budgetSearchRequest.getMonth(),budgetSearchRequest.getYear(),budgetSearchRequest.getUserId());
    }

    public void updateBudget(BudgetRequest budgetRequest) {
        budgetRepository.updateBudgetRecord(budgetRequest);
    }

    public void addIncome(IncomeEvent incomeEvent) {
        double sumAddition = incomeEvent.getIncomeRequest().getAmount() * budgetPercentage;
        incomeEvent.getIncomeRequest().setDate( incomeEvent.getIncomeRequest().getDate().plusMonths(1));
        incomeEvent.getIncomeRequest().setAmount(sumAddition);
        BudgetRequest request =getBudgetRequest(incomeEvent);
        boolean budgetCreated = createBudget(request);
        if(!budgetCreated){
            addSumToBudget(request);
        }
    }

    private BudgetRequest getBudgetRequest(IncomeEvent incomeEvent) {
        LocalDate date = incomeEvent.getIncomeRequest().getDate();
        return BudgetRequest.builder().month(date.getMonthValue()).year(date.getYear()).userId(incomeEvent.getUserId()).sum(incomeEvent.getIncomeRequest().getAmount()).build();
    }

    private BudgetSearchRequest getBudgetSearchRequest(IncomeEvent incomeEvent) {
        LocalDate date = incomeEvent.getIncomeRequest().getDate();
        return BudgetSearchRequest.builder().month(date.getMonthValue()).year(date.getYear()).userId(incomeEvent.getUserId()).build();
    }

    private void  addSumToBudget(BudgetRequest budgetRequest)
    {
       budgetRepository.addBudgetSum(budgetRequest);
    }

    public boolean addExpense(ExpenseEvent expenseEvent) {
        boolean hasSufficientFunds = false;
        BudgetRequest request = getBudgetRequestFromExpense (expenseEvent);
        boolean budgetCreated = createBudget(request); // doesn't have budget, need to create one (with sum of zero)
        if(!budgetCreated){
            hasSufficientFunds = addExpenseToBudget(request);
        }
        return hasSufficientFunds; // is budget is created the boolean won't change, otherwise it can go both ways.
    }

    private boolean addExpenseToBudget(BudgetRequest request) {
        boolean hasSufficientFunds = false;
        BudgetData budgetData = budgetRepository.findBudgetDataByMonthAndYearAndUserId(request.getMonth(), request.getYear(), request.getUserId());
        if(budgetData.getSum() - budgetData.getMoneySpent() >= request.getMoneySpent()) //Sufficient Funds
        {
            hasSufficientFunds = true;
            budgetRepository.addExpenseToBudget(request);
        }
        return hasSufficientFunds;
    }

    private BudgetRequest getBudgetRequestFromExpense(ExpenseEvent expenseEvent) {
        LocalDate date =expenseEvent.getExpenseRequest().getDate();
        return BudgetRequest.builder().month(date.getMonthValue()).year(date.getYear()).
                userId(expenseEvent.getUserId()).sum(0d).moneySpent(expenseEvent.getExpenseRequest().getSum()).build();
    }


}
