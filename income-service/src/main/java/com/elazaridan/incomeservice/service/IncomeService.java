package com.elazaridan.incomeservice.service;

import com.elazaridan.incomeservice.dto.income.IncomePutRequest;
import com.elazaridan.incomeservice.dto.income.IncomeRequest;
import com.elazaridan.incomeservice.dto.income.IncomeResponse;
import com.elazaridan.incomeservice.model.IncomeData;
import com.elazaridan.incomeservice.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;

    public IncomeResponse createIncomeEntry(String userId, IncomeRequest incomeRequest) {
        if(incomeRequest.getDate() == null)
            incomeRequest.setDate(LocalDate.now());
        IncomeData incomeData = IncomeData.builder().source(incomeRequest.getSource()).
                amount(incomeRequest.getAmount()).date(incomeRequest.getDate()).userId(userId).build();
        incomeRepository.save(incomeData);
        log.info("Income record from {} with amount {} was created",incomeData.getSource(),incomeData.getAmount());
        return IncomeResponse.builder().source(incomeData.getSource()).date(incomeData.getDate()).amount(incomeData.getAmount()).build();
    }

    public List<IncomeResponse> getAllIncomesEntries() {
        return convertIncomeDateToIncomeResponse(incomeRepository.findAll());
    }

    public List<IncomeResponse> getAllIncomesEntriesWithSameSource(String source) {
        return convertIncomeDateToIncomeResponse(incomeRepository.findAllBySource(source));
    }

    public List<IncomeResponse> getAllIncomesEntriesWithSameDate(LocalDate date) {
        return convertIncomeDateToIncomeResponse(incomeRepository.findAllByDate(date));
    }

    public List<IncomeResponse>  getAllElementsWithDateInRange(LocalDate startDate, LocalDate endDate) {
        return convertIncomeDateToIncomeResponse(incomeRepository.findAllByDateGreaterThanEqualAndDateLessThanEqual(startDate,endDate));
    }

    private List<IncomeResponse> convertIncomeDateToIncomeResponse(List<IncomeData> listOfIncomeData)
    {
        return listOfIncomeData.stream().map(incomeData -> IncomeResponse.builder().
                source(incomeData.getSource()).amount(incomeData.getAmount()).date(incomeData.getDate()).build()).toList();
    }

    public List<IncomeResponse> getAllIncomeOfUser(String userId) {
        List<IncomeData> allIncomesByUserId = incomeRepository.findAllByUserId(userId);
        return allIncomesByUserId.stream().map(incomeData -> IncomeResponse.builder().
                source(incomeData.getSource()).amount(incomeData.getAmount()).date(incomeData.getDate()).build()).toList();
    }

    public void deleteIncome(Long incomeId)
    {
        incomeRepository.deleteById(incomeId);

    }

    public void updateIncomeRecord(IncomePutRequest incomePutRequest) {

        incomeRepository.updateIncomeRecord(incomePutRequest.getIncomeId(),incomePutRequest.getSource(),incomePutRequest.getAmount(),incomePutRequest.getDate());
    }

    public IncomeResponse getIncomeById(String incomeId) {
        IncomeData incomeData = incomeRepository.findById(Long.parseLong(incomeId)).get();
        return IncomeResponse.builder().source(incomeData.getSource()).date(incomeData.getDate()).amount(incomeData.getAmount()).build();
    }
}
