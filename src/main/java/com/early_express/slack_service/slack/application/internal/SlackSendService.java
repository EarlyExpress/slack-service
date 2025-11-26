package com.early_express.slack_service.slack.application.internal;

import com.early_express.slack_service.slack.application.exception.SlackException;
import com.early_express.slack_service.slack.domain.MessageSend;
import com.early_express.slack_service.slack.domain.entity.MessageType;
import com.early_express.slack_service.slack.domain.entity.Slack;
import com.early_express.slack_service.slack.domain.entity.SlackId;
import com.early_express.slack_service.slack.domain.entity.SlackStatus;
import com.early_express.slack_service.slack.domain.repository.SlackRepository;
import com.early_express.slack_service.slack.infrastructure.client.dto.request.SendRequest;
import com.early_express.slack_service.slack.infrastructure.client.dto.response.SendResponse;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.TaskScheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
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

    // 메시지 전송 + DB 저장
    public void sendDeliveryMessage(SendRequest sendRequest) throws Exception {
        if (sendRequest.getReceiverId() == null || sendRequest.getReceiverId().isEmpty()) {
            throw new SlackException(MISSING_PARAMETER);
        }

        boolean callResult = callSlack(sendRequest);
        SlackStatus status = callResult ? SlackStatus.SENT : SlackStatus.FAILED;
        String errorMessage = callResult ? null : SLACK_SEND_FAILED.getMessage();
        LocalDateTime sentAt = callResult ? LocalDateTime.now() : null;

        Slack slack = Slack.builder()
                .slackId(SlackId.of())
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

    // 🔹 스케줄러: no-arg, DB 조회 혹은 더미 데이터 생성
    @Scheduled(cron = "0 59 12 * * *") // 매일 12:40
    //@SchedulerLock(name = "sendDeliveryMessageLock") // MSA 환경 안전
    @CacheEvict(value = "slack", allEntries = true)
    public void scheduleDelivery() {
        try {
            // 1. DB에서 배송자 리스트 조회 (실제 시나리오)
            List<String> receivers = List.of("U09V1GT3BH8"); // 테스트용 더미 데이터
            for (String receiver : receivers) {
                SendRequest request = SendRequest.builder()
                        .receiverId(receiver)
                        .message("오늘 배송할 주소: 서울시 강남구 ...") // 테스트용 메시지
                        .messageType(MessageType.MORNING_DELIVERY)
                        .build();
                sendDeliveryMessage(request);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
