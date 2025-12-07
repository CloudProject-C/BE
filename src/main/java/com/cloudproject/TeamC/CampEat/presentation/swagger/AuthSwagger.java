package com.cloudproject.TeamC.CampEat.presentation.swagger;

import com.cloudproject.TeamC.CampEat.dto.request.EmailVerifyRequest;
import com.cloudproject.TeamC.CampEat.dto.request.UserJoinRequest;
import com.cloudproject.TeamC.CampEat.dto.request.UserLoginRequest;
import com.cloudproject.TeamC.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Auth", description = "인증 및 회원가입 API")
public interface AuthSwagger {

    @Operation(summary = "이메일 인증 코드 전송", description = "회원가입을 위해 이메일로 인증 코드를 전송합니다.")
    CommonResponse<String> sendEmail(
            @Parameter(description = "인증할 이메일 주소", example = "test@khu.ac.kr")
            @RequestParam String email);

    @Operation(summary = "이메일 인증 코드 확인", description = "전송된 인증 코드를 검증합니다.")
    CommonResponse<Void> verifyCode(@RequestBody EmailVerifyRequest requestDto);

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    CommonResponse<Void> join(@RequestBody UserJoinRequest userJoinRequestDto);

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 토큰을 발급받습니다.")
    CommonResponse<String> login(@RequestBody UserLoginRequest userLoginDto);
}