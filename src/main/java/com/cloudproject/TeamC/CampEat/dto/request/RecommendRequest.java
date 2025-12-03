package com.cloudproject.TeamC.CampEat.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecommendRequest {
    private List<String> features; // 새로 받은 10개 특징
}