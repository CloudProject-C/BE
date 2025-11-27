package com.cloudproject.TeamC.CampEat.presentation;

import com.cloudproject.TeamC.CampEat.dto.request.ReviewCreateRequest;
import com.cloudproject.TeamC.CampEat.application.ReviewService;
import com.cloudproject.TeamC.global.common.CommonResponse;
import com.cloudproject.TeamC.global.common.code.SuccessCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reviews")
@Tag(name = "Review", description = "리뷰 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<List<String>> createReview(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        // TODO: SecurityContext에서 실제 로그인한 userId를 가져오도록 수정 필요
        Long mockUserId = 1L;

        ReviewCreateRequest request;
        try {
            request = new ObjectMapper().readValue(requestJson, ReviewCreateRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 실패: " + e.getMessage());
        }
        List<String> uploadImageUrls = reviewService.createReview(mockUserId, request, images);
        return CommonResponse.success(SuccessCode.REVIEW_CREATE_SUCCESS, uploadImageUrls);
    }
}