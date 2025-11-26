package com.early_express.slack_service.slack.infrastructure.repository;

import com.early_express.slack_service.slack.infrastructure.client.dto.response.SendResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SlackRepositoryCustom {
    Page<SendResponse> slackList(int page, int size,String sortType);
}
