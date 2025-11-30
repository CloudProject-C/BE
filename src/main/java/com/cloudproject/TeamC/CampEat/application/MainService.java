package com.cloudproject.TeamC.CampEat.application;

import com.cloudproject.TeamC.CampEat.domain.School;
import com.cloudproject.TeamC.CampEat.domain.User;
import com.cloudproject.TeamC.CampEat.dto.response.MainPageResponse;
import com.cloudproject.TeamC.CampEat.exception.CampEatException;
import com.cloudproject.TeamC.CampEat.exception.code.CampEatErrorCode;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.PlaceRepository;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.ReviewRepository;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PlaceRepository placeRepository;

    public MainPageResponse getMainPageInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CampEatException(CampEatErrorCode.USER_NOT_FOUND));

        School school = user.getSchool();
        Long schoolId = school.getId();

        // 1. 학교 이름
        String schoolName = school.getName();

        // 2. 학교 소속 유저 수
        Long userCount = userRepository.countBySchoolId(schoolId);

        // 3. 학교 소속 리뷰 수
        Long reviewCount = reviewRepository.countBySchoolId(schoolId);

        // 4. 학교 근처 음식점 수 (예: 반경 2km 이내)
        Long placeCount = placeRepository.countPlacesNearSchool(school.getCampusLocation(), 2000.0);

        return MainPageResponse.builder()
                .schoolName(schoolName)
                .schoolUserCount(userCount)
                .schoolReviewCount(reviewCount)
                .schoolPlaceCount(placeCount)
                .build();
    }
}