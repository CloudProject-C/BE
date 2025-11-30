package com.cloudproject.TeamC.CampEat.dto.response;

import com.cloudproject.TeamC.CampEat.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;

@Builder
@Schema(description = "마이 페이지 정보 응답")
public record MyPageResponse(
        @Schema(description = "닉네임", example = "캠핑장인")
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "https://s3.../profile.jpg")
        String profileImage,

        @Schema(description = "학교 이름", example = "경희대학교")
        String schoolName,

        @Schema(description = "작성한 리뷰 수", example = "15")
        Long myReviewCount,

        @Schema(description = "좋아요 누른 장소 수", example = "8")
        Long myPlaceLikeCount,

        @Schema(description = "선호 음식 카테고리 목록", example = "[\"KOREAN\", \"WESTERN\"]")
        List<String> foodPreferences
) {
    public static MyPageResponse of(User user, Long reviewCount, Long placeLikeCount, List<String> preferences) {
        return MyPageResponse.builder()
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .schoolName(user.getSchool().getName())
                .myReviewCount(reviewCount)
                .myPlaceLikeCount(placeLikeCount)
                .foodPreferences(preferences)
                .build();
    }
}