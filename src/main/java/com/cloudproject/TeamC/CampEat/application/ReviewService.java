package com.cloudproject.TeamC.CampEat.application;

import com.cloudproject.TeamC.CampEat.domain.*;
import com.cloudproject.TeamC.CampEat.dto.request.ReviewCreateRequest;
import com.cloudproject.TeamC.CampEat.dto.response.ReviewResponse;
import com.cloudproject.TeamC.CampEat.exception.code.CampEatErrorCode;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.*;
import com.cloudproject.TeamC.CampEat.exception.CampEatException;
import com.cloudproject.TeamC.global.util.LocationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final S3Service s3Service;

    private static final double ALLOWED_DISTANCE_KM = 0.5;

    @Transactional
    public List<String> createReview(Long userId, ReviewCreateRequest request, List<MultipartFile> images) {
        // 1. 사용자 및 음식점 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CampEatException(CampEatErrorCode.USER_NOT_FOUND));

        Place place = placeRepository.findById(request.placeId())
                .orElseThrow(() -> new CampEatException(CampEatErrorCode.PLACE_NOT_FOUND));

        // 2. [검증] 이미지 개수 제한 (0~5장)
        if (images != null && images.size() > 5) {
            throw new CampEatException(CampEatErrorCode.REVIEW_IMAGE_LIMIT_EXCEEDED);
        }

        // 3. [검증] 학교 근처인지 확인 (거리 계산)
        validateSchoolProximity(user.getSchool(), place);

        // 4. 리뷰 저장
        Review review = Review.builder()
                .user(user)
                .place(place)
                .rating(request.rating())
                .content(request.content())
                .build();

        List<String> uploadedImageUrls = new ArrayList<>();

        // 5. 이미지 업로드 및 Review에 추가 (Cascade 활용)
        if (images != null && !images.isEmpty()) {
            for (MultipartFile file : images) {
                String imageUrl = s3Service.upload(file);
                // String imageUrl = "https://dummy-s3-url.com/image.jpg";

                uploadedImageUrls.add(imageUrl);

                // ReviewImage 객체 생성 (review 필드는 addImage에서 설정됨)
                ReviewImage reviewImage = ReviewImage.builder()
                        .imageUrl(imageUrl)
                        .build();

                review.addImage(reviewImage);
            }
        }

        // 6. Review 저장
        reviewRepository.save(review);

        return uploadedImageUrls;
    }

    public Page<ReviewResponse> getReviews(Long placeId, String sortType, int page, int size, Long currentUserId) {
        Pageable pageable = createPageable(sortType, page, size);
        Page<Review> reviewPage;

        if ("LIKES".equalsIgnoreCase(sortType)) {
            reviewPage = reviewRepository.findAllByPlaceIdOrderByLikesDesc(placeId, PageRequest.of(page, size));
        } else {
            reviewPage = reviewRepository.findAllByPlaceIdAndIsHiddenFalse(placeId, pageable);
        }

        // currentUserId를 DTO 생성 메서드에 전달
        return reviewPage.map(review -> {
            long likeCount = reviewRepository.countLikesByReviewId(review.getId());
            return ReviewResponse.from(review, likeCount, currentUserId);
        });
    }

    private Pageable createPageable(String sortType, int page, int size) {
        Sort sort = switch (sortType.toUpperCase()) {
            case "LATEST" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "OLDEST" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "RATING_HIGH" -> Sort.by(Sort.Direction.DESC, "rating");
            case "RATING_LOW" -> Sort.by(Sort.Direction.ASC, "rating");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };

        if ("LIKES".equalsIgnoreCase(sortType)) {
            return PageRequest.of(page, size);
        }

        return PageRequest.of(page, size, sort);
    }

    private void validateSchoolProximity(School school, Place place) {
        double distance = LocationUtil.calculateDistance(school.getCampusLocation(), place.getLocation());

        if (distance > ALLOWED_DISTANCE_KM) {
            throw new CampEatException(CampEatErrorCode.PLACE_TOO_FAR_FROM_SCHOOL);
        }
    }
}