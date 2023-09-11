package com.elazaridan.incomeservice.repository;

import com.elazaridan.incomeservice.model.IncomeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<IncomeData,Long> {
    public List<IncomeData> findAllBySource(String source);
    public List<IncomeData> findAllByDate(LocalDate date);
    public List<IncomeData> findAllByDateGreaterThanEqualAndDateLessThanEqual(LocalDate startDate, LocalDate endDate);
    public List<IncomeData> findAllByUserId(String userId);

    @Transactional
    @Modifying
    @Query("UPDATE IncomeData i " +
            "SET i.source = COALESCE(:source, i.source), " +
            "i.amount = COALESCE(:amount, i.amount), " +
            "i.date = COALESCE(:date, i.date) " +
            "WHERE i.id = :incomeId")
    void updateIncomeRecord(Long incomeId, String source, Double amount, LocalDate date);

}
