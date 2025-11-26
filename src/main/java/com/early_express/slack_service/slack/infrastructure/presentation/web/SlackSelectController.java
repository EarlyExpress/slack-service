package com.early_express.slack_service.slack.infrastructure.presentation.web;


import com.early_express.slack_service.global.common.utils.PageUtils;
import com.early_express.slack_service.global.presentation.dto.PageResponse;
import com.early_express.slack_service.slack.application.web.SlackSelectService;
import com.early_express.slack_service.slack.domain.entity.Slack;
import com.early_express.slack_service.slack.infrastructure.client.dto.response.SendResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/slack/web")
@RequiredArgsConstructor
public class SlackSelectController {

    private final SlackSelectService slackSelectService;

    @GetMapping("/selectAll")
    public PageResponse<SendResponse> selectAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LATEST") String sortType
    ) {
//        Page<SendResponse> slackPage = slackSelectService.getSelectAll(page, size, sortType);
//
//
//        return PageUtils.toPageResponse(slackPage);
        return slackSelectService.getSelectAll(page, size, sortType);
    }

//    public ResponseEntity<Page<SendResponse>> selectAll(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "LATEST") String sortType){
//        Page<SendResponse> response = slackSelectService.getSelectAll(page,size,sortType);
//        return ResponseEntity.ok(response);
//    }
}
