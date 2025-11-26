package com.early_express.slack_service.slack.infrastructure.repository;

import com.early_express.slack_service.slack.domain.entity.QSlack;
import com.early_express.slack_service.slack.infrastructure.client.dto.response.SendResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SlackRepositoryImpl implements SlackRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SendResponse> slackList(int page, int size,String sortType) {
        QSlack slack = QSlack.slack;
        long offset = (long)page * size;

        JPAQuery<SendResponse> query = queryFactory
                .select(Projections.constructor(SendResponse.class,
                        slack.slackId,
                        slack.receiverSlackId,
                        slack.message,
                        slack.type,
                        slack.status,
                        slack.sentAt,
                        slack.errorMessage))
                .from(slack)
                .orderBy(slack.sentAt.desc());

        List<SendResponse> list = query
                .offset(offset)
                .limit(size)
                .fetch();



        Long total = queryFactory
                .select(slack.count())
                .from(slack)
                .fetchOne();

        return new PageImpl<>(list, PageRequest.of(page,size), total);
    }
}

