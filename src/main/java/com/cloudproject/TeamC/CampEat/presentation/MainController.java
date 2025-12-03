package com.cloudproject.TeamC.CampEat.presentation;

import com.cloudproject.TeamC.CampEat.application.MainService;
import com.cloudproject.TeamC.CampEat.domain.User;
import com.cloudproject.TeamC.CampEat.dto.response.MainPageResponse;
import com.cloudproject.TeamC.CampEat.presentation.swagger.MainSwagger;
import com.cloudproject.TeamC.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cloudproject.TeamC.global.common.code.SuccessCode.FETCH_MAIN_PAGE_SUCCESS;

@Tag(name = "Main", description = "메인 페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/main")
public class MainController implements MainSwagger {

    private final MainService mainService;

    @Override
    @GetMapping
    public CommonResponse<MainPageResponse> getMainPage(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        return CommonResponse.success(FETCH_MAIN_PAGE_SUCCESS, mainService.getMainPageInfo(userId));
    }
}