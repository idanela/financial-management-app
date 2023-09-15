package com.elazaridan.budgetservice.controller;

import com.elazaridan.budgetservice.dto.budget.BudgetRequest;
import com.elazaridan.budgetservice.dto.budget.BudgetResponse;
import com.elazaridan.budgetservice.dto.budget.BudgetSearchRequest;
import com.elazaridan.budgetservice.model.BudgetData;
import com.elazaridan.budgetservice.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/budget")
@RequiredArgsConstructor
public class BudgetController {

        private final BudgetService budgetService;

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public ResponseEntity createBudget(@Valid @RequestBody BudgetRequest budgetRequest){
                boolean isEntryCreated = budgetService.createBudget(budgetRequest);
                if (isEntryCreated) {
                    return ResponseEntity.status(HttpStatus.CREATED).body("Budget created successfully");
                }
                else
                {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(String.format("Budget for %d/%d already exists", budgetRequest.getMonth(), budgetRequest.getYear()));
                }
        }

        @GetMapping
        @ResponseStatus(HttpStatus.OK)
        public List<BudgetData> getAllBudgets()
        {
             return budgetService.getALLBudgets();
        }

        @GetMapping("/search")
        @ResponseStatus(HttpStatus.OK)
        public ResponseEntity<?> getBudget(@Valid @RequestBody BudgetSearchRequest budgetSearchRequest)
        {
                BudgetResponse budgetByMonthAndYear = budgetService.getBudgetByMonthAndYear(budgetSearchRequest);
               if(budgetByMonthAndYear!=null)
               {
                return ResponseEntity.status(HttpStatus.OK).body(budgetByMonthAndYear.toString());
               }

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Budget for %d/%d was not found",budgetSearchRequest.getMonth(),budgetSearchRequest.getYear()));
        }

        @DeleteMapping()
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void deleteBudget(@Valid @RequestBody BudgetSearchRequest budgetSearchRequest)
        {
            budgetService.deleteBudget(budgetSearchRequest);
        }

        @PutMapping()
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void updateBudget(@Valid @RequestBody BudgetRequest budgetRequest)
        {
            budgetService.updateBudget(budgetRequest);
        }
    
}
