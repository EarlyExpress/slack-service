package com.early_express.slack_service.slack.application.event;

import lombok.Generated;
import lombok.Getter;

@Getter
public class BaseEvent {
    private String eventId;
    private String eventType;
    private String sourceService;
    private String timestamp;
}
