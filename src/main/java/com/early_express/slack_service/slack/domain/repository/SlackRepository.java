package com.early_express.slack_service.slack.domain.repository;

import com.early_express.slack_service.slack.domain.entity.Slack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SlackRepository extends JpaRepository<Slack, UUID> {

}

