package com.cloudproject.TeamC.CampEat.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EmailVerifyRequest (
        @NotBlank(message = "[ERROR] 이메일 입력은 필수 입니다.")
        String email,

        @NotBlank(message = "[ERROR] 코드 입력은 필수 입니다.")
        String code
) {
}