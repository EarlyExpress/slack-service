package com.early_express.slack_service.slack.infrastructure.kafka;

import com.early_express.slack_service.slack.application.event.NotificationRequestedEvent;
import com.early_express.slack_service.slack.application.internal.SlackSendService;
import io.swagger.v3.oas.annotations.headers.Header;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequestedEventConsumer {

    private final SlackSendService slackSendService;

    @KafkaListener(
            topics = "${spring.kafka.topic.notification-requested-event}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(NotificationRequestedEvent event) throws Exception {
        log.info("NotificationRequested 이벤트 수신 - orderId: {}, eventId: {}",
                event.getOrderId(), event.getEventId());

        slackSendService.sendHubMessage(event);
    }

}