package com.early_express.slack_service.slack.domain.entity;

import com.early_express.slack_service.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "slack")
@Getter
@ToString
@Entity
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Slack extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="slack_id", length=20)
    private UUID slackId;

    private String receiverSlackId;

    private String message;

    private LocalDateTime sendAt;

    @Enumerated(EnumType.STRING)
    private SlackStatus status;

    @Enumerated(EnumType.STRING)
    private MessageType type;

}
