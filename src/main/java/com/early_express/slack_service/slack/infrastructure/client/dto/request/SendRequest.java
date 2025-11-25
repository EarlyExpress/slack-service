package com.early_express.slack_service.slack.infrastructure.client.dto.request;

import com.early_express.slack_service.slack.domain.entity.MessageType;
import com.early_express.slack_service.slack.domain.entity.SlackStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendRequest {
    private String receiverId;
    private String message;
    private MessageType messageType;
    private SlackStatus slackStatus;
}
