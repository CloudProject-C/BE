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


    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody UserJoinRequest userJoinRequestDto) {
        authService.registerUser(userJoinRequestDto);
        return ResponseEntity.ok("Registration successful");
    }


}