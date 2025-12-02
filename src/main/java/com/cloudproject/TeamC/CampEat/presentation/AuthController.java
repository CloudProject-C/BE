package com.cloudproject.TeamC.CampEat.presentation;


import com.cloudproject.TeamC.CampEat.application.AuthService;
import com.cloudproject.TeamC.CampEat.application.EmailService;
import com.cloudproject.TeamC.CampEat.application.UserService;
import com.cloudproject.TeamC.CampEat.dto.request.EmailVerifyRequest;
import com.cloudproject.TeamC.CampEat.dto.request.UserJoinRequest;
import com.cloudproject.TeamC.CampEat.dto.request.UserLoginRequest;
import com.cloudproject.TeamC.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.cloudproject.TeamC.global.common.code.ErrorCode.BAD_REQUEST;
import static com.cloudproject.TeamC.global.common.code.SuccessCode.SEND_EMAIL_SUCCESS;
import static com.cloudproject.TeamC.global.common.code.SuccessCode.VERIFY_EMAIL_SUCCESSS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

    @PostMapping("/email/send-email")
    public CommonResponse<String> sendEmail(@RequestParam String email) throws Exception {
        return CommonResponse.success(SEND_EMAIL_SUCCESS, emailService.sendMessage(email));
    }

    @PostMapping("/email/verify")
    public CommonResponse<String> verifyCode(@RequestBody EmailVerifyRequest requestDto) {
        boolean check = emailService.verifyCode(requestDto);
        if (check) {
            return CommonResponse.success(VERIFY_EMAIL_SUCCESSS,"인증 완료!");
        }
        else {
            return CommonResponse.failure(BAD_REQUEST, "인증 실패");
        }
    }


    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody UserJoinRequest userJoinRequestDto) {
        authService.registerUser(userJoinRequestDto);
        return ResponseEntity.ok("Registration successful");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequest userLoginDto) {
        try {
            String token = authService.login(userLoginDto.getEmail(), userLoginDto.getPassword());
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Invalid password");
        }
    }


}