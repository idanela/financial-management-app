package com.elazaridan.incomeservice.listener;

import com.elazaridan.incomeservice.dto.income.IncomePutRequest;
import com.elazaridan.incomeservice.dto.income.IncomeResponse;
import com.elazaridan.incomeservice.dto.events.IncomeEvent;
import com.elazaridan.incomeservice.service.IncomeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class Listeners {

    private final IncomeService incomeService;
    private final ObjectMapper objectMapper;


    @RabbitListener(queues = "income-queue")
    public String  createIncomeEntry(String incomeEventJson, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        IncomeEvent incomeEvent = objectMapper.readValue(incomeEventJson, IncomeEvent.class);
        channel.basicAck(deliveryTag, false); // send an ACK to rabbitMQ server (otherwise each time we'll restart the app all unAcked messages will be sent again.)
        IncomeResponse incomeResponse = incomeService.createIncomeEntry(incomeEvent.getUserId(),incomeEvent.getIncomeRequest());
        String jsonResponse = objectMapper.writeValueAsString(incomeResponse);

        return  jsonResponse;
    }

    @RabbitListener(queues = "income-retrieve-queue")
    public String GetAllIncomeOfUser(String userId, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException
    {
        List<IncomeResponse> incomeResponses = incomeService.getAllIncomeOfUser(userId);
        try {
            String jsonIncomeResponses = objectMapper.writeValueAsString(incomeResponses);
            channel.basicAck(deliveryTag, false); // send an ACK to rabbitMQ server (otherwise each time we'll restart the app all unAcked messages will be sent again.)

            return jsonIncomeResponses;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
    @RabbitListener(queues = "income-by-id-queue")
    public String GetIncomeById(String IncomeId, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException
    {
        IncomeResponse incomeResponse = incomeService.getIncomeById(IncomeId);
        try {
            String jsonIncomeResponses = objectMapper.writeValueAsString(incomeResponse);
            channel.basicAck(deliveryTag, false); // send an ACK to rabbitMQ server (otherwise each time we'll restart the app all unAcked messages will be sent again.)

            return jsonIncomeResponses;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RabbitListener(queues = "income-delete-queue")
    public void deleteIncomeOfUser(long incomeId, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException
    {
      incomeService.deleteIncome(incomeId);
      channel.basicAck(deliveryTag, false); // send an ACK to rabbitMQ server (otherwise each time we'll restart the app all unAcked messages will be sent again.)
    }

    @RabbitListener(queues = "income-update-queue")
    public void putIncomeOfUser(String IncomePutRequestJsonPayload, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException
    {
        IncomePutRequest incomePutRequest = objectMapper.readValue(IncomePutRequestJsonPayload,IncomePutRequest.class);
        incomeService.updateIncomeRecord(incomePutRequest);
        channel.basicAck(deliveryTag, false); // send an ACK to rabbitMQ server (otherwise each time we'll restart the app all unAcked messages will be sent again.)
    }
}
