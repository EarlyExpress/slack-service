package com.early_express.slack_service.slack.domain.entity;

import com.early_express.slack_service.global.infrastructure.entity.BaseEntity;
import com.early_express.slack_service.slack.domain.MessageSend;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
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

    @EmbeddedId
    private SlackId slackId;

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



//    @Builder
//    public Slack(UUID slackId, String receiverSlackId, String message, SlackStatus status, MessageType type, String errorMessage, LocalDateTime sendAt, MessageSend messageSend) {
//        this.slackId = slackId;
//        if( receiverSlackId == null || receiverSlackId.isEmpty() ) this.receiverSlackId = UUID.randomUUID().toString();
//        this.message = message;
//        this.sendAt = sendAt;
//        this.status = status;
//        this.type = type;
//        this.errorMessage = errorMessage;
//        this.sendAt = LocalDateTime.now();
//        setMessage(messageSend);
//    }
//
//    private void setMessage(MessageSend messageSend) {
//        if (messageSend == null) return;
//        List<String> ids = List.of(this.receiverSlackId);
//        String message = this.message;
//        boolean result = messageSend.send(ids,message);
//    }
}
