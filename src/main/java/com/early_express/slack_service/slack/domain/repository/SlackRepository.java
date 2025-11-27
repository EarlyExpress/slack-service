package com.early_express.slack_service.slack.domain.repository;

import com.early_express.slack_service.slack.domain.entity.Slack;
import com.early_express.slack_service.slack.domain.entity.SlackStatus;
import com.early_express.slack_service.slack.infrastructure.client.dto.response.SendResponse;
import com.early_express.slack_service.slack.infrastructure.repository.SlackRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SlackRepository extends JpaRepository<Slack, UUID>, SlackRepositoryCustom {

    Page<SendResponse> slackList(int page, int size,String sortType);

    List<Slack> findByStatus(SlackStatus slackStatus);
}

