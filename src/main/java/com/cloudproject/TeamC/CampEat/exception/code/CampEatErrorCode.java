package com.cloudproject.TeamC.CampEat.exception.code;

import com.cloudproject.TeamC.global.common.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CampEatErrorCode implements BaseCode {

    REVIEW_IMAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "리뷰 이미지는 최대 5장까지 업로드 가능합니다."),
    PLACE_TOO_FAR_FROM_SCHOOL(HttpStatus.FORBIDDEN, "자신의 학교 근처 음식점에만 리뷰를 작성할 수 있습니다."),
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 음식점입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}