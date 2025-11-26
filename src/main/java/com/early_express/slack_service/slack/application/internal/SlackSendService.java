package com.early_express.slack_service.slack.application.internal;

import com.early_express.slack_service.slack.application.exception.SlackException;
import com.early_express.slack_service.slack.domain.MessageSend;
import com.early_express.slack_service.slack.domain.entity.MessageType;
import com.early_express.slack_service.slack.domain.entity.Slack;
import com.early_express.slack_service.slack.domain.entity.SlackStatus;
import com.early_express.slack_service.slack.domain.repository.SlackRepository;
import com.early_express.slack_service.slack.infrastructure.client.dto.request.SendRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.early_express.slack_service.global.presentation.exception.GlobalErrorCode.MISSING_PARAMETER;
import static com.early_express.slack_service.slack.application.exception.SlackErrorCode.*;


@Service
@RequiredArgsConstructor
@Transactional
public class SlackSendService {
    private final SlackRepository slackRepository;
    private final MessageSend messageSend;

    public void sendDeliveryMessage(SendRequest sendRequest) throws Exception {
        if (sendRequest.getReceiverId() == null || sendRequest.getReceiverId().isEmpty()) {
            throw new SlackException(MISSING_PARAMETER);
        }

        boolean callResult = callSlack(sendRequest);
        SlackStatus status = callResult ? SlackStatus.SENT : SlackStatus.FAILED;
        String errorMessage = callResult ? null : SLACK_SEND_FAILED.getMessage();
        LocalDateTime sentAt = callResult ? LocalDateTime.now() : null;

        Slack slack = Slack.builder()
                .receiverSlackId(sendRequest.getReceiverId())
                .message(sendRequest.getMessage())
                .type(sendRequest.getMessageType())
                .status(status)
                .type(MessageType.MORNING_DELIVERY)
                .sentAt(sentAt)
                .errorMessage(errorMessage)
                .build();

        try {
            slackRepository.save(slack);
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

    @Scheduled(cron = "0 1 14 * * *")
    //@SchedulerLock(name = "sendDeliveryMessageLock")
    @CacheEvict(value = "slack", allEntries = true)
    public void scheduleDelivery() {
        try {
            List<String> receivers = List.of("U09V1GT3BH8");
            for (String receiver : receivers) {
                SendRequest request = SendRequest.builder()
                        .receiverId(receiver)
                        .message("오늘 배송할 주소: 서울시 강남구 ...")
                        .messageType(MessageType.MORNING_DELIVERY)
                        .build();
                sendDeliveryMessage(request);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
