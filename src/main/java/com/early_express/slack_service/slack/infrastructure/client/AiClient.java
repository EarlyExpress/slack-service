package com.early_express.slack_service.slack.infrastructure.client;

import com.early_express.slack_service.global.config.FeignConfig;
import com.early_express.slack_service.slack.infrastructure.client.dto.request.AiRequest;
import com.early_express.slack_service.slack.infrastructure.client.dto.response.AiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ai-service",configuration = FeignConfig.class)
public interface AiClient {
    @GetMapping("shipment/notify")

//    AiResponse getAi();
    String getAi();
}
