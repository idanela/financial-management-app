package com.elazaridan.incomeservice.controller;
import com.elazaridan.incomeservice.dto.income.IncomeResponse;
import com.elazaridan.incomeservice.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/income")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<IncomeResponse> getAllIncomeEntries()
    {
        return  incomeService.getAllIncomesEntries();
    }

    @GetMapping("source/{source}")
    @ResponseStatus(HttpStatus.OK)
    public List<IncomeResponse> getAllIncomeEntriesWithSameSource(@PathVariable String source)
    {
        return  incomeService.getAllIncomesEntriesWithSameSource(source);
    }
    @GetMapping("date/{date}")
    @ResponseStatus(HttpStatus.OK)
    public List<IncomeResponse> getAllIncomeEntriesWithSameDate(@PathVariable LocalDate date)
    {
        return  incomeService.getAllIncomesEntriesWithSameDate(date);
    }
    @GetMapping("date/range")
    @ResponseStatus(HttpStatus.OK)
    public List<IncomeResponse>  getAllElementsWithDateInRange(@RequestParam(required = true) LocalDate startDate, @RequestParam(required = true) LocalDate endDate)
    {
        return incomeService.getAllElementsWithDateInRange(startDate,endDate);
    }
}
