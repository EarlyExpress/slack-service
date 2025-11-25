package com.early_express.slack_service.slack.application.exception;

import com.early_express.slack_service.global.presentation.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum SlackErrorCode implements ErrorCode {
    SLACK_NOT_FOUND("SLACK_001","슬랙 아이디를 찾을 수 없습니다.",404),
    SLACK_RETRY_FAILED("SLACK", "재시도 횟수 초가로 전송 실패하였습니다.",400),
    SLACK_DB_SAVE_FAILED("SLACK", "DB저장에 실패하였습니다.",400),
    SLACK_SEND_FAILED("SLACK","메세지 전송에 실패하였습니다.",400),
    SLACK_SCHEDULE_MISS_TIME("SLACK", "요청하신 시간이 아닙니다.",400),
    SLACK_CHANEL_CREATE_FAILED("SLACK","채널 생성 실패하였습니다.",400);
    private final String code;
    private final String message;
    private final int status;
}
