package com.cloudproject.TeamC.CampEat.presentation;

import com.cloudproject.TeamC.CampEat.application.ReviewService;
import com.cloudproject.TeamC.CampEat.domain.User;
import com.cloudproject.TeamC.CampEat.dto.request.ReviewCreateRequest;
import com.cloudproject.TeamC.CampEat.dto.response.ReviewResponse;
import com.cloudproject.TeamC.CampEat.presentation.swagger.ReviewSwagger;
import com.cloudproject.TeamC.global.common.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.cloudproject.TeamC.global.common.code.SuccessCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reviews")
public class ReviewController implements ReviewSwagger {

    private final ReviewService reviewService;

    @Override
    @PostMapping
    public CommonResponse<List<String>> createReview(
            @Valid @RequestPart("request") ReviewCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,@AuthenticationPrincipal User user
    ) {
        Long userId = user.getId();

//        ReviewCreateRequest request;
//        try {
//            request = new ObjectMapper().readValue(requestJson, ReviewCreateRequest.class);
//        } catch (Exception e) {
//            throw new RuntimeException("JSON 파싱 실패: " + e.getMessage());
//        }
        List<String> uploadImageUrls = reviewService.createReview(userId, request, images);
        return CommonResponse.success(REVIEW_CREATE_SUCCESS, uploadImageUrls);
    }

    @Override
    @GetMapping("/{placeId}")
    public CommonResponse<Page<ReviewResponse>> getReviews(
            @PathVariable Long placeId,
            @RequestParam String sort,
            @RequestParam int page,
            @RequestParam int size,
            @AuthenticationPrincipal User user
    ) {
        Long userId = user.getId();
        Page<ReviewResponse> response = reviewService.getReviews(placeId, sort, page, size, userId);

        return CommonResponse.success(FETCH_REVIEW_SUCCESS, response);
    }

    @Override
    @PostMapping("/{reviewId}/like")
    public CommonResponse<Void> toggleReviewLike(
            @PathVariable Long reviewId, @AuthenticationPrincipal User user
    ) {
        Long userId = user.getId();
        reviewService.toggleReviewLike(reviewId, userId);
        return CommonResponse.success(REVIEW_LIKE_TOGGLE_SUCCESS);
    }
}