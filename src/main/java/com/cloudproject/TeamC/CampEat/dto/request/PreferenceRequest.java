package com.cloudproject.TeamC.CampEat.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PreferenceRequest {
    private Long userId;
    private List<String> features; // 특징 10개 (문자열 리스트)
}