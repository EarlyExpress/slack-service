package com.early_express.slack_service.slack.domain.entity;

import com.early_express.slack_service.global.infrastructure.entity.BaseEntity;
import com.early_express.slack_service.slack.domain.MessageSend;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "slack")
@Getter
@ToString
@Entity
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Slack extends BaseEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    @GeneratedValue(strategy = GenerationType.UUID)
//    private SlackId slackId;
    private UUID slackId;

    @Column(nullable=false)
    private String receiverSlackId;


    private String message;

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private SlackStatus status;

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private MessageType type;

    private LocalDateTime sentAt;

    private String errorMessage;


}
