package com.cloudproject.TeamC.CampEat.presentation;

import com.cloudproject.TeamC.CampEat.application.UserService;
import com.cloudproject.TeamC.CampEat.dto.response.MyLikedPlaceResponse;
import com.cloudproject.TeamC.CampEat.dto.response.MyPageResponse;
import com.cloudproject.TeamC.CampEat.dto.response.MyReviewResponse;
import com.cloudproject.TeamC.CampEat.presentation.swagger.UserSwagger;
import com.cloudproject.TeamC.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.cloudproject.TeamC.global.common.code.SuccessCode.*;

@Tag(name = "User (MyPage)", description = "마이 페이지 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController implements UserSwagger {

    private final UserService userService;

    @Override
    @GetMapping("/me")
    public CommonResponse<MyPageResponse> getMyPage() {
        // TODO: SecurityContext에서 userId
        Long mockUserId = 1L;
        return CommonResponse.success(FETCH_MY_PAGE_SUCCESS, userService.getMyPageInfo(mockUserId)); // SuccessCode는 적절한 것으로 대체 가능
    }

    @Override
    @GetMapping("/me/reviews")
    public CommonResponse<Page<MyReviewResponse>> getMyReviews(
            @RequestParam(defaultValue = "LATEST") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long mockUserId = 1L;
        return CommonResponse.success(FETCH_MY_REVIEWS_SUCCESS, userService.getMyReviews(mockUserId, sort, page, size));
    }

    @Override
    @GetMapping("/me/likes")
    public CommonResponse<Page<MyLikedPlaceResponse>> getMyLikedPlaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long mockUserId = 1L;
        // 찜한 순서(최신순) 정렬
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return CommonResponse.success(FETCH_MY_LIKED_PLACES_SUCCESS, userService.getMyLikedPlaces(mockUserId, pageRequest));
    }
}