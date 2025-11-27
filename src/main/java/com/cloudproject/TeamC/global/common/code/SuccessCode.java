package com.cloudproject.TeamC.global.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements BaseCode{

    // health_check
    HEALTH_CHECK_SUCCESS(HttpStatus.OK, "Health Check Success"),
    REVIEW_CREATE_SUCCESS(HttpStatus.CREATED, "리뷰 작성이 성공적으로 완료되었습니다."),
    FETCH_REVIEW_SUCCESS(HttpStatus.OK,"리뷰 조회가 성공적으로 완료되었습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
