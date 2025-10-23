package com.cloudproject.TeamC.global.presentation;

import com.cloudproject.TeamC.global.common.CommonResponse;
import com.cloudproject.TeamC.global.presentation.swagger.GlobalSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cloudproject.TeamC.global.common.code.SuccessCode.HEALTH_CHECK_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/global")
public class GlobalController implements GlobalSwagger {

    @Override
    @GetMapping("/health-check")
    public CommonResponse<String> healthCheck() {
        return CommonResponse.success(HEALTH_CHECK_SUCCESS, "OK");
    }
}