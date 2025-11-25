package com.early_express.slack_service.slack.infrastructure.slack;

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

@Slf4j
@Service
@RefreshScope
public class SlackMessageSend implements MessageSend {
    @Value("${SLACK_TOKEN}")
    private String token;

    @Override
    public boolean send(List<String> ids, String message) {

       if (ids != null) {

            RestClient client = RestClient.builder()
                    .baseUrl("https://slack.com/api")
                    .build();



            // Channel ID 처리 S
            ResponseEntity<JsonNode> response = client.post()
                    .uri("/conversations.open")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("users", String.join(",", ids)))
                    .retrieve()
                    .toEntity(JsonNode.class);

           System.out.println(response.getBody() + " 응답");
            JsonNode node = response.getBody();

            if (!response.getStatusCode().is2xxSuccessful() ||  node.get("ok") == null || !node.get("ok").toString().equals("true")) return false;

            String channelId = node.get("channel").get("id").textValue();
            // Channel ID 처리 E

            // 메세지 발송 처리 S
            response = client.post()
                    .uri("/chat.postMessage")
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of("channel", channelId, "text", message, "as_user", true))
                    .contentType(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(JsonNode.class);

            return response.getStatusCode().is2xxSuccessful() && node.get("ok") != null && node.get("ok").toString().equals("true");
            // 메세지 발송 처리 E
        }


        return false;
    }
}

