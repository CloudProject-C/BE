package com.cloudproject.TeamC.global.util;

public class DtoUtil {

    // 인스턴스화 방지
    private DtoUtil() {}

    /**
     * 카테고리 전체 경로에서 간소화된 카테고리명(2차 분류)만 추출
     * 예: "음식점 > 카페 > 테마카페" -> "카페"
     */
    public static String extractSimpleCategory(String fullCategoryName) {
        if (fullCategoryName == null || fullCategoryName.isBlank()) {
            return "기타";
        }
        String[] categories = fullCategoryName.split(" > ");

        // 길이가 2 이상이면 두 번째(인덱스 1) 요소 반환
        if (categories.length > 1) {
            return categories[1];
        }

        // 구조가 다르거나 " > "가 없으면 전체 반환
        return fullCategoryName;
    }
}
