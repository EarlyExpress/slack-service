package com.early_express.slack_service.slack.domain;

import java.util.List;

public interface MessageSend {
    boolean send(List<String> ids, String message);
}
