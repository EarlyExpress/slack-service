package com.early_express.slack_service.slack.application.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationRequestedEvent extends BaseEvent {
    private String orderId;
    private String receiverName;
    private String receiverPhone;
    private String receiverEmail;
    private String orderNumber;
    private String estimatedDeliveryTime;
    private String deliveryAddress;
    private String notificationType;
    private String requestedAt;

}
