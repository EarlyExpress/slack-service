package com.early_express.slack_service.slack.infrastructure.presentation.web;


import com.early_express.slack_service.global.common.utils.PageUtils;
import com.early_express.slack_service.global.presentation.dto.PageResponse;
import com.early_express.slack_service.slack.application.web.SlackSelectService;
import com.early_express.slack_service.slack.domain.entity.Slack;
import com.early_express.slack_service.slack.infrastructure.client.dto.response.SendResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/slack/web")
@RequiredArgsConstructor
public class SlackSelectController {

    private final SlackSelectService slackSelectService;

    @GetMapping("/select")
    public PageResponse<SendResponse> selectAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LATEST") String sortType
    ) {
        return slackSelectService.getSelectAll(page, size, sortType);
    }

    @GetMapping("/select/{id}")
    public ResponseEntity<SendResponse> select(@PathVariable UUID id) {
        SendResponse response = slackSelectService.getOneSlack(id);
        return ResponseEntity.ok(response);

    }

}
