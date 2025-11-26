package com.early_express.slack_service.slack.application.web;


import com.early_express.slack_service.global.common.utils.PageUtils;
import com.early_express.slack_service.global.presentation.dto.PageResponse;
import com.early_express.slack_service.slack.domain.entity.Slack;
import com.early_express.slack_service.slack.domain.repository.SlackRepository;
import com.early_express.slack_service.slack.infrastructure.client.dto.response.SendResponse;
import com.early_express.slack_service.slack.infrastructure.repository.SlackRepositoryImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SlackSelectService {
    private final SlackRepositoryImpl slackRepositoryImpl;

    @Transactional(readOnly = true)
    @Cacheable(value = "slack", key = " #page + ':' + #size + ':' + #sortType")
    public PageResponse<SendResponse> getSelectAll(int page, int size, String sortType) {

        Page<SendResponse> slackPage = slackRepositoryImpl.slackList(page, size, sortType);

        return PageUtils.toPageResponse(slackPage);
    }
}
