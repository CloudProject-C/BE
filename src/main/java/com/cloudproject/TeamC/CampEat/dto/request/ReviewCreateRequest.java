package com.cloudproject.TeamC.CampEat.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewCreateRequest(
        @NotNull(message = "음식점 ID는 필수입니다.")
        Long placeId,

        @Min(1) @Max(5)
        @NotNull(message = "별점은 필수입니다.")
        Integer rating,

        @NotBlank(message = "리뷰 내용은 필수입니다.")
        String content
) {}
