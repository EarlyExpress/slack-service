package com.early_express.slack_service.slack.infrastructure.presentation.internal;


import com.early_express.slack_service.slack.application.event.NotificationRequestedEvent;
import com.early_express.slack_service.slack.application.internal.SlackSendService;
import com.early_express.slack_service.slack.infrastructure.client.dto.request.SendRequest;
import com.early_express.slack_service.slack.infrastructure.client.dto.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/slack/internal")
@RequiredArgsConstructor
public class SlackSendController {

    private final SlackSendService slackSendService;

    @PostMapping("/hub-sent")
    public ResponseEntity<NotificationRequestedEvent> sendOrderMessage(@RequestBody NotificationRequestedEvent notificationRequestedEvent) throws Exception {

        NotificationRequestedEvent event =slackSendService.sendHubMessage(notificationRequestedEvent);
        return ResponseEntity.ok(event);

    }

    // ai를 통해 정보 받기 (외부호출)
//    @PostMapping("/test")
//    public void sendTest(@RequestBody SendRequest request) {
//        slackSendService.sendDeliveryMessage(request);
//    }


}
