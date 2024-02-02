package com.elazaridan.budgetservice.repository;

import com.elazaridan.budgetservice.dto.budget.BudgetRequest;
import com.elazaridan.budgetservice.model.BudgetData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.withinPercentage;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@DataJpaTest
class BudgetRepositoryTest {

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    BudgetRepository budgetRepository;

    BudgetData budgetData;

    @BeforeEach
    void setUp() {
        if (budgetData == null) {
            budgetData = BudgetData.builder().sum(100d).moneySpent(20d).month(12).year(2023).userId("Idan").build();
            budgetRepository.save(budgetData);
        }
    }

    @AfterEach
    void tearDown() {
        // Clean up the test data
        budgetRepository.deleteById(budgetData.getBudgetId());
    }


    @Test
    void CheckIfBudgetRecordUpdated() {
        BudgetData updatedBudget = null;
        //Act
        budgetData.setSum(150d);
        budgetRepository.save(budgetData);

        try {
            updatedBudget = budgetRepository.findById(budgetData.getBudgetId()).get();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //Assert
        assertThat(updatedBudget).isNotNull();
        assertThat(updatedBudget.getSum()).isEqualTo(150d);
    }
    @Test
    void checkAddBudgetSum() {

        // Arrange
        BudgetRequest budgetRequest = BudgetRequest.builder().sum(100d).month(12).year(2023).userId("Idan").moneySpent(0d).build();

        // Act
        try {
            budgetRepository.addBudgetSum(budgetRequest);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
        entityManager.clear();
        BudgetData updatedBudget = budgetRepository.findById(budgetData.getBudgetId()).get();
            // Assert
            assertThat(updatedBudget).isNotNull();
            assertThat(updatedBudget.getSum()).isEqualTo(200d);

    }
        @Test
        void addExpenseToBudget () {
            // Arrange
            BudgetRequest budgetRequest = BudgetRequest.builder().sum(0d).month(12).year(2023).userId("Idan").moneySpent(30d).build();

            // Act
            try {
                budgetRepository.addExpenseToBudget(budgetRequest);
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            }
            entityManager.clear();
            BudgetData budgetData = budgetRepository.findById(this.budgetData.getBudgetId()).get();
           //Assert
            assertThat(budgetData).isNotNull();
            assertThat(budgetData.getMoneySpent()).isEqualTo(50);
        }
}
