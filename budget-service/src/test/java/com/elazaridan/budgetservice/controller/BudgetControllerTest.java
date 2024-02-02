package com.elazaridan.budgetservice.controller;

import com.elazaridan.budgetservice.dto.budget.BudgetRequest;
import com.elazaridan.budgetservice.dto.budget.BudgetResponse;
import com.elazaridan.budgetservice.dto.budget.BudgetSearchRequest;
import com.elazaridan.budgetservice.model.BudgetData;
import com.elazaridan.budgetservice.service.BudgetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = BudgetController.class)
class BudgetControllerTest {

    private MockMvc mockMvc;
    @MockBean
    private BudgetService budgetService;
    private ObjectMapper objectMapper;


    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new BudgetController(budgetService)).defaultRequest(get("/").accept(MediaType.APPLICATION_JSON)).build();
        this.objectMapper = new ObjectMapper();
    }


    @Test
    void createBudget() throws Exception {
        //Arrange
        BudgetRequest budgetRequest = BudgetRequest.builder().moneySpent(20d).year(2020).month(3).sum(150d).userId("Idan").build();

        when(budgetService.createBudget(any(BudgetRequest.class))).thenReturn(true);

        //Act + Assert
        this.mockMvc.perform(post("/api/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(budgetRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void getAllBudgets() throws Exception {

        List<BudgetData> mockBudgets = Arrays.asList(
                BudgetData.builder().sum(50d).month(5).year(2024).build(),
                BudgetData.builder().sum(100).month(12).year(2024).build());
        when(budgetService.getALLBudgets()).thenReturn(mockBudgets);
        this.mockMvc.perform(get("/api/budget"))  // Adjusted the endpoint URL
                .andExpect(status().isOk())  // Adjusted the status check
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].sum").value(50.0))
                .andExpect(jsonPath("$[0].month").value(5))
                .andExpect(jsonPath("$[0].year").value(2024))
                .andExpect(jsonPath("$[1].sum").value(100.0))
                .andExpect(jsonPath("$[1].month").value(12))
                .andExpect(jsonPath("$[1].year").value(2024));
    }


    @Test
    void getBudget() throws Exception {
        // Arrange
        BudgetResponse budgetResponse = BudgetResponse.builder().year(2020).month(9).sum(150).build();
        when(budgetService.getBudgetByMonthAndYear(any(BudgetSearchRequest.class))).thenReturn(budgetResponse);

        // Act
        BudgetSearchRequest budgetSearchRequest = BudgetSearchRequest.builder().month(10).year(2020).userId("Idan").build();

        // Assert
        this.mockMvc.perform(get("/api/budget/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(budgetSearchRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteBudget() throws Exception {
        BudgetSearchRequest budgetSearchRequest = BudgetSearchRequest.builder().userId("Idan").month(12).year(2025).build();

        doNothing().when(budgetService).deleteBudget(any(BudgetSearchRequest.class));

        this.mockMvc.perform(delete("/api/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(budgetSearchRequest)))
                .andExpect(status().isNoContent());


    }

    @Test
    void updateBudget() throws Exception{
        BudgetRequest budgetRequest = BudgetRequest.builder().userId("Idan").month(12).year(2025).build();

        doNothing().when(budgetService).updateBudget(any(BudgetRequest.class));

        this.mockMvc.perform(put("/api/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(budgetRequest)))
                .andExpect(status().isNoContent());

    }
}