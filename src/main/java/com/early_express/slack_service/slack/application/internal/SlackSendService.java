package com.early_express.slack_service.slack.application.internal;

import com.early_express.slack_service.global.presentation.exception.GlobalException;
import com.early_express.slack_service.slack.application.event.NotificationRequestedEvent;
import com.early_express.slack_service.slack.application.exception.SlackException;
import com.early_express.slack_service.slack.domain.MessageSend;
import com.early_express.slack_service.slack.domain.entity.MessageType;
import com.early_express.slack_service.slack.domain.entity.Slack;
import com.early_express.slack_service.slack.domain.entity.SlackStatus;
import com.early_express.slack_service.slack.domain.repository.SlackRepository;
import com.early_express.slack_service.slack.infrastructure.client.AiClient;
import com.early_express.slack_service.slack.infrastructure.client.dto.request.AiRequest;
import com.early_express.slack_service.slack.infrastructure.client.dto.request.SendRequest;
import com.early_express.slack_service.slack.infrastructure.client.dto.response.AiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.early_express.slack_service.global.presentation.exception.GlobalErrorCode.INTERNAL_SERVER_ERROR;
import static com.early_express.slack_service.global.presentation.exception.GlobalErrorCode.MISSING_PARAMETER;
import static com.early_express.slack_service.slack.application.exception.SlackErrorCode.*;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SlackSendService {
    private final SlackRepository slackRepository;
    private final MessageSend messageSend;
    private final AiClient aiClient;

    public void sendDeliveryMessage(SendRequest sendRequest) throws Exception {
        System.out.println("sendDeliveryMessage");
        if (sendRequest.getReceiverId() == null || sendRequest.getReceiverId().isEmpty()) {
            throw new SlackException(MISSING_PARAMETER);
        }

        boolean callResult = callSlack(sendRequest);
        System.out.println("callResult: " + callResult);
        SlackStatus status = callResult ? SlackStatus.SENT : SlackStatus.FAILED;
        String errorMessage = callResult ? null : SLACK_SEND_FAILED.getMessage();
        LocalDateTime sentAt = callResult ? LocalDateTime.now() : null;

        try {
            Slack slack = Slack.builder()
                    .receiverSlackId(sendRequest.getReceiverId())
                    .message(sendRequest.getMessage())
                    .type(sendRequest.getMessageType())
                    .status(status)
                    .type(MessageType.MORNING_DELIVERY)
                    .sentAt(sentAt)
                    .errorMessage(errorMessage)
                    .build();


//            slackRepository.save(slack);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SlackException(SLACK_DB_SAVE_FAILED);
        }
    }

    public boolean callSlack(SendRequest sendRequest) throws Exception {
        if (messageSend == null) return false;
        List<String> id = List.of(sendRequest.getReceiverId());
        return messageSend.send(id, sendRequest.getMessage());
    }

    public NotificationRequestedEvent sendHubMessage(NotificationRequestedEvent notificationRequestedEvent) throws Exception {
        if (notificationRequestedEvent.getReceiverPhone() == null || notificationRequestedEvent.getReceiverPhone().isEmpty()) {
            throw new SlackException(MISSING_PARAMETER);
        }

        // 실제 Slack 호출은 하지 않고 항상 성공 처리
        boolean callResult = callHubSlack(notificationRequestedEvent);
        SlackStatus status = callResult ? SlackStatus.SENT : SlackStatus.FAILED;
        String errorMessage = callResult ? null : SLACK_SEND_FAILED.getMessage();
        LocalDateTime sentAt = callResult ? LocalDateTime.now() : null;

        try {
            Slack slack = Slack.builder()
                    .orderId(notificationRequestedEvent.getOrderId())
                    .receiverSlackId(notificationRequestedEvent.getReceiverPhone())
                    .message(notificationRequestedEvent.getDeliveryAddress())
                    .type(MessageType.HUB_ORDER)
                    .status(status)
                    .notification(notificationRequestedEvent.getNotificationType())
                    .sentAt(sentAt)
                    .errorMessage(errorMessage)
                    .build();


            slackRepository.save(slack);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SlackException(SLACK_DB_SAVE_FAILED);
        }

        return notificationRequestedEvent;
    }

    public boolean callHubSlack(NotificationRequestedEvent notificationRequestedEvent) throws Exception {
        if (messageSend == null) return false;
        List<String> id = List.of(notificationRequestedEvent.getReceiverPhone());
        return messageSend.send(id, notificationRequestedEvent.getDeliveryAddress());
    }

    // ai 메세지 호출
    @Scheduled(cron = "0 48 20 * * *")
    @CacheEvict(value = "slackDrafts", allEntries = true)
    public void fetchAiMessages() throws Exception {
        try {

            String message = aiClient.getAi();

            if (message == null || message.isEmpty()) {
                throw new SlackException(AI_REQUEST_FAILED);
            }

            Slack slack = Slack.builder()
                    .receiverSlackId("U09V1GT3BH8")
                    .message(message)
                    .status(SlackStatus.PENDING)
                    .type(MessageType.MORNING_DELIVERY)
                    .build();
            slackRepository.save(slack);
//            scheduleDelivery();

        } catch (Exception e) {
            log.error("fetchAiMessages", e);
            throw new SlackException(INTERNAL_SERVER_ERROR);
        }
    }





    @Scheduled(cron = "0  49 20 * * *")
    //@SchedulerLock(name = "sendDeliveryMessageLock")
    @CacheEvict(value = "slack", allEntries = true)
    public void scheduleDelivery() {
        System.out.println("여기 들어옴");

        try {
            List<Slack> pendingMessages = slackRepository.findByStatus(SlackStatus.PENDING);

//            List<String> receivers = List.of("U09V1GT3BH8");

            for (Slack slack : pendingMessages) {
                SendRequest request = SendRequest.builder()
                        .receiverId("U09V1GT3BH8")
                        .message(slack.getMessage())
                        .messageType(slack.getType())
                        .build();
                try {
                    sendDeliveryMessage(request);
                    slack.updateStatus(SlackStatus.SENT);
                } catch (Exception e) {
                    slack.updateStatus(SlackStatus.FAILED);
                    slack.updateErrorMessage(e.getMessage());
                }
//                slackRepository.save(slack);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

