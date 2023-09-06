package com.elazaridan.userservice.controller;

import com.elazaridan.userservice.dto.Income.IncomePutRequest;
import com.elazaridan.userservice.dto.Income.IncomeRequest;
import com.elazaridan.userservice.dto.Income.IncomeResponse;
import com.elazaridan.userservice.dto.user.UserRequest;
import com.elazaridan.userservice.dto.user.UserResponse;
import com.elazaridan.userservice.dto.events.IncomeEvent;
import com.elazaridan.userservice.model.User;
import com.elazaridan.userservice.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {


    private final RabbitTemplate rabbitTemplate;
    private final UserService userService;
    private final ObjectMapper objectMapper; // Jackson ObjectMapper

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CachePut(value = "users-cache",key = "#result.id")
    public UserResponse createUser(@RequestBody UserRequest userRequest )
    {
        return userService.createUser(userRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getAllUsers()
    {
        return userService.getAllUsers();
    }

    @Cacheable(value = "users-cache")
    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable String userId)
    {
        return userService.getUser(userId);
    }

    @CacheEvict(value = "users-cache", allEntries = false)
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable String userId)
    {
        userService.deleteUser(userId);
    }


    //Income
    @PostMapping("/add-income/{userId}")
    public IncomeResponse addIncomeToUser(
            @PathVariable String userId,
            @RequestBody IncomeRequest incomeRequest) {
        IncomeResponse incomeCreated = null;
        IncomeEvent incomeEvent = new IncomeEvent(userId, incomeRequest);
        try {
            String IncomeEventJsonPayload = objectMapper.writeValueAsString(incomeEvent);
            String jsonResponse =(String) rabbitTemplate.convertSendAndReceive("income-exchange", "income-routing-key", IncomeEventJsonPayload);
             incomeCreated = objectMapper.readValue(jsonResponse, new TypeReference<IncomeResponse>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return incomeCreated;
    }

    @GetMapping("/income/user/{userId}")
    public List<IncomeResponse> getIncomeOfUser(@PathVariable String userId)
    {
        try {
            String responseJson = (String) rabbitTemplate.convertSendAndReceive("income-exchange", "income-retrieve-routing-key", userId);
            List<IncomeResponse> incomeResponses = objectMapper.readValue(responseJson,new TypeReference<List<IncomeResponse>>() {});
            return incomeResponses;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }
    @GetMapping("/income/{incomeId}")
    public IncomeResponse getIncomeById(@PathVariable String incomeId)
    {
        IncomeResponse incomeResponse = null;
        try {
            String responseJson = (String) rabbitTemplate.convertSendAndReceive("income-exchange", "income-by-id-routing-key", incomeId);
            incomeResponse = objectMapper.readValue(responseJson,new TypeReference<IncomeResponse>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return incomeResponse;
    }

    @DeleteMapping ("income/delete/{incomeId}")
    public  ResponseEntity<String> deleteIncomeOfUser(@PathVariable long incomeId)
    {
        try {
            rabbitTemplate.convertAndSend("income-exchange", "income-delete-routing-key", incomeId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok("Income delete request sent for processing");
    }

    @PutMapping("income/update/{incomeId}")
    public  ResponseEntity<String> updateIncome(@PathVariable long incomeId,@RequestBody IncomeRequest incomeRequest) {
        IncomePutRequest incomePutRequest = new IncomePutRequest(incomeId,incomeRequest);
        try {
            String IncomePutRequestJsonPayload = objectMapper.writeValueAsString(incomePutRequest);
            rabbitTemplate.convertAndSend("income-exchange", "income-update-routing-key", IncomePutRequestJsonPayload);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("Income update request sent for processing");

    }
}
