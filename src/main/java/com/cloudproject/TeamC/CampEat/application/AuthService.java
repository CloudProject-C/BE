package com.cloudproject.TeamC.CampEat.application;


import com.cloudproject.TeamC.CampEat.domain.School;
import com.cloudproject.TeamC.CampEat.domain.User;
import com.cloudproject.TeamC.CampEat.dto.request.UserJoinRequest;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.SchoolRepository;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.UserRepository;
import com.cloudproject.TeamC.global.common.code.ErrorCode;
import com.cloudproject.TeamC.global.config.JwtTokenProvider;
import com.cloudproject.TeamC.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SchoolRepository schoolRepository;


    public void registerUser(UserJoinRequest userJoinRequestDto) {
        School school = schoolRepository.findById(userJoinRequestDto.getSchoolId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN));

        User user = userJoinRequestDto.toEntity(school, passwordEncoder);
        userRepository.save(user);
    }

    public String login(String email, String userPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(userPassword, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtTokenProvider.createToken(email);
    }
}