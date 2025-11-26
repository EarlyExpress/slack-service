package com.early_express.slack_service.slack.infrastructure.client.dto.response;


import com.early_express.slack_service.slack.domain.entity.MessageType;
import com.early_express.slack_service.slack.domain.entity.Slack;
import com.early_express.slack_service.slack.domain.entity.SlackStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record SendResponse (
        UUID slackId,
        String receiverSlackId,
        String message,
        MessageType messageType,
        SlackStatus status,
        LocalDateTime sentAt,
        String errorMessage
) {
    public static SendResponse of(Slack slack) {
        return SendResponse.builder()
                .slackId(slack.getSlackId())
                .receiverSlackId(slack.getReceiverSlackId())
                .message(slack.getMessage())
                .messageType(slack.getType())
                .status(slack.getStatus())
                .sentAt(slack.getSentAt())
                .errorMessage(slack.getErrorMessage())
                .build();
    }
}
