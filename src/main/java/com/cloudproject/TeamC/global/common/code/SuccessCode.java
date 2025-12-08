package com.cloudproject.TeamC.global.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements BaseCode{

    // health_check
    HEALTH_CHECK_SUCCESS(HttpStatus.OK, "Health Check Success"),

    // Review
    REVIEW_CREATE_SUCCESS(HttpStatus.CREATED, "리뷰 작성이 성공적으로 완료되었습니다."),
    FETCH_REVIEW_SUCCESS(HttpStatus.OK,"리뷰 조회가 성공적으로 완료되었습니다."),
    REVIEW_LIKE_TOGGLE_SUCCESS(HttpStatus.OK, "리뷰 좋아요/취소가 완료되었습니다."),

    // Place
    FETCH_PLACE_SUCCESS(HttpStatus.OK, "음식점 상세 조회가 성공적으로 완료되었습니다."),
    FETCH_NEARBY_PLACES_SUCCESS(HttpStatus.OK, "주변 음식점 조회가 성공적으로 완료되었습니다."),
    PLACE_LIKE_TOGGLE_SUCCESS(HttpStatus.OK, "음식점 좋아요/취소가 완료되었습니다."),

    // Main
    FETCH_MAIN_PAGE_SUCCESS(HttpStatus.OK, "메인 페이지 조회가 성공적으로 완료되었습니다."),

    // User / Auth
    USER_JOIN_SUCCESS(HttpStatus.CREATED, "회원가입이 성공적으로 완료되었습니다."),
    USER_LOGIN_SUCCESS(HttpStatus.OK, "로그인이 성공적으로 완료되었습니다."),
    SEND_EMAIL_SUCCESS(HttpStatus.OK, "인증코드가 성공적으로 전송되었습니다."),
    VERIFY_EMAIL_SUCCESSS(HttpStatus.OK, "코드 인증이 완료되었습니다."),
    FETCH_MY_PAGE_SUCCESS(HttpStatus.OK, "마이 페이지 조회가 성공적으로 완료되었습니다."),
    FETCH_MY_REVIEWS_SUCCESS(HttpStatus.OK, "내가 쓴 리뷰 목록 조회가 성공적으로 완료되었습니다."),
    FETCH_MY_LIKED_PLACES_SUCCESS(HttpStatus.OK, "내가 좋아요한 음식점 목록 조회가 성공적으로 완료되었습니다."),

    // Preference
    PREFERENCE_ONBOARDING_SUCCESS(HttpStatus.ACCEPTED, "온보딩 정보 수신 완료. 백그라운드에서 저장 중입니다."),
    RECOMMEND_REQUEST_SUCCESS(HttpStatus.ACCEPTED, "추천 계산 요청이 성공적으로 접수되었습니다."),
    FETCH_RECOMMENDATION_SUCCESS(HttpStatus.OK, "추천 음식점 목록 조회가 성공적으로 완료되었습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
