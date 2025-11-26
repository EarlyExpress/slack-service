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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.TaskScheduler;

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
    private final MessageSend  messageSend;
    private final TaskScheduler taskScheduler;


    public void sendDeliveryMessage(SendRequest sendRequest) throws Exception {
        if (sendRequest.getReceiverId() == null || sendRequest.getReceiverId().isEmpty()) {
            throw new SlackException(MISSING_PARAMETER);
        }


        boolean call_result = callSlack(sendRequest);
        SlackStatus status = call_result ? SlackStatus.SENT : SlackStatus.FAILED;
        String errorMessage = call_result ? null : SLACK_SEND_FAILED.getMessage();
        LocalDateTime sentAt = call_result ?LocalDateTime.now(): null ;


        // DB 저장
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
        } catch(Exception e){
            e.printStackTrace();
            throw new SlackException(SLACK_DB_SAVE_FAILED);

        }
    }


    // api 호출
    public boolean callSlack( SendRequest sendRequest) throws Exception {
        if(messageSend == null) return false;
        List<String> id =List.of(sendRequest.getReceiverId());
        boolean result = messageSend.send(id, sendRequest.getMessage());

        return result;

    }

    // 스케줄링
    @CacheEvict(value = "slack", allEntries = true)
    public SendResponse schedule_delivery(SendRequest sendRequest) throws Exception{


        Runnable schedule_delivery = new Runnable()
        {
            @Override
            public void run() {
                try {
                     sendDeliveryMessage(sendRequest);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        taskScheduler.schedule(schedule_delivery, new CronTrigger("0 39 11 * * *"));




        return new SendResponse(

                SlackId.of().getId(), sendRequest.getReceiverId(),sendRequest.getMessage(),sendRequest.getMessageType(),sendRequest.getSlackStatus(),LocalDateTime.now(),"");
    }



}

