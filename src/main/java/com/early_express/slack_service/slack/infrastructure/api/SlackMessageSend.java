package com.early_express.slack_service.slack.infrastructure.api;

import com.early_express.slack_service.global.presentation.exception.GlobalException;
import com.early_express.slack_service.slack.application.exception.SlackErrorCode;
import com.early_express.slack_service.slack.application.exception.SlackException;
import com.early_express.slack_service.slack.domain.MessageSend;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

import static com.early_express.slack_service.slack.application.exception.SlackErrorCode.SLACK_CHANEL_CREATE_FAILED;
import static com.early_express.slack_service.slack.application.exception.SlackErrorCode.SLACK_SEND_FAILED;

@Slf4j
@Service
@RefreshScope
public class SlackMessageSend implements MessageSend {
    @Value("${SLACK_TOKEN}")
    private String token;

    @Override
    public boolean send(List<String> ids, String message) {
        try {
            if (ids == null || ids.isEmpty()) {
                throw new IllegalArgumentException("Slack ID가 없습니다.");
            }

            RestClient client = RestClient.builder()
                    .baseUrl("https://slack.com/api")
                    .build();

            // 채널 생성
            ResponseEntity<JsonNode> response = client.post()
                    .uri("/conversations.open")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("users", String.join(",", ids)))
                    .retrieve()
                    .toEntity(JsonNode.class);

            JsonNode node = response.getBody();
            if (!response.getStatusCode().is2xxSuccessful() ||
                    node.get("ok") == null || !node.get("ok").asBoolean()) {
                throw new SlackException(SLACK_CHANEL_CREATE_FAILED);
            }

            String channelId = node.get("channel").get("id").asText();

            // 메시지 발송
            response = client.post()
                    .uri("/chat.postMessage")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("channel", channelId, "text", message, "as_user", true))
                    .retrieve()
                    .toEntity(JsonNode.class);

            node = response.getBody();
            if (!response.getStatusCode().is2xxSuccessful() ||
                    node.get("ok") == null || !node.get("ok").asBoolean()) {
                throw new SlackException(SLACK_SEND_FAILED);
            }

            return true;

        } catch (Exception e) {
            // 로깅 후 Service로 예외 던짐
            log.error("Slack API 호출 실패", e);
            throw new SlackException(SLACK_SEND_FAILED,e.getMessage());

        }
    }

}

