package com.early_express.slack_service.slack.application.exception;

import com.early_express.slack_service.global.presentation.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum SlackErrorCode implements ErrorCode {
    SLACK_NOT_FOUND("SLACK_001","슬랙 아이디를 찾을 수 없습니다.",404),
    SLACK_DB_SAVE_FAILED("SLACK_002", "DB저장에 실패하였습니다.",500),
    SLACK_SEND_FAILED("SLACK_003","메세지 전송에 실패하였습니다.",502),
    SLACK_SCHEDULE_MISS_TIME("SLACK_004", "요청하신 시간이 아닙니다.",400),
    SLACK_CHANEL_CREATE_FAILED("SLACK_005","채널 생성 실패하였습니다.",500),
    AI_REQUEST_FAILED("AI_001", "AI 호출 실패하였거나 값이 없습니다",500)
    ;

    private final String code;
    private final String message;
    private final int status;
}
