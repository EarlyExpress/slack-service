package com.early_express.slack_service.slack.infrastructure.client.dto.response;


import com.early_express.slack_service.slack.domain.entity.SlackStatus;
import com.early_express.slack_service.slack.infrastructure.client.dto.request.SendRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendResponse {
    private UUID slackId;
    private String receiverSlackId;
    private SlackStatus status;
    private LocalDateTime sentAt;
    private String errorMessage;


}
