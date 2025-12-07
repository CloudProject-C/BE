package com.cloudproject.TeamC.CampEat.dto.request;

import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "로그인 요청")
public class UserLoginRequest {
    @Schema(description = "이메일", example = "test@khu.ac.kr")
    private String email;

    @Schema(description = "비밀번호", example = "password1234")
    private String password;
}