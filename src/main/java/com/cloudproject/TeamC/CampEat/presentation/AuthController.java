package com.cloudproject.TeamC.CampEat.presentation;


import com.cloudproject.TeamC.CampEat.application.AuthService;
import com.cloudproject.TeamC.CampEat.application.EmailService;
import com.cloudproject.TeamC.CampEat.dto.request.EmailVerifyRequest;
import com.cloudproject.TeamC.CampEat.dto.request.UserJoinRequest;
import com.cloudproject.TeamC.CampEat.dto.request.UserLoginRequest;
import com.cloudproject.TeamC.CampEat.presentation.swagger.AuthSwagger;
import com.cloudproject.TeamC.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.cloudproject.TeamC.global.common.code.ErrorCode.BAD_REQUEST;
import static com.cloudproject.TeamC.global.common.code.SuccessCode.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthSwagger {

    private final AuthService authService;
    private final EmailService emailService;

    @Override
    @PostMapping("/email/send-email")
    public CommonResponse<String> sendEmail(@RequestParam String email) {
        return CommonResponse.success(SEND_EMAIL_SUCCESS, emailService.sendMessage(email));
    }

    @Override
    @PostMapping("/email/verify")
    public CommonResponse<Void> verifyCode(@RequestBody EmailVerifyRequest requestDto) {
        boolean check = emailService.verifyCode(requestDto);
        if (check) {
            return CommonResponse.success(VERIFY_EMAIL_SUCCESSS);
        }
        else {
            return CommonResponse.failure(BAD_REQUEST);
        }
    }

    @Override
    @PostMapping("/join")
    public CommonResponse<Void> join(@RequestBody UserJoinRequest userJoinRequestDto) {
        authService.registerUser(userJoinRequestDto);
        return CommonResponse.success(USER_JOIN_SUCCESS);
    }

    @Override
    @PostMapping("/login")
    public CommonResponse<String> login(@RequestBody UserLoginRequest userLoginDto) {
        String token = authService.login(userLoginDto.getEmail(), userLoginDto.getPassword());
        log.info("로그인 요청");
//        logCapture.capture("로그인 요청 logCapture");
        return CommonResponse.success(USER_LOGIN_SUCCESS, token);
    }


}