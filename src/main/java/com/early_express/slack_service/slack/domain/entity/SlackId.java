package com.early_express.slack_service.slack.domain.entity;

import com.fasterxml.jackson.annotation.JacksonInject;
import jakarta.persistence.Embeddable;
import jakarta.persistence.NamedEntityGraph;
import lombok.*;

import java.util.UUID;

@ToString
@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SlackId {
    private UUID id;

    protected SlackId(UUID id) {
        this.id = id;
    }


    public static SlackId of(UUID id) {
        return new SlackId(id);
    }

    public static SlackId of() {
        return SlackId.of(UUID.randomUUID());
    }

}
