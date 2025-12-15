package com.cloudproject.TeamC.CampEat.application;


import com.cloudproject.TeamC.CampEat.domain.School;
import com.cloudproject.TeamC.CampEat.domain.User;
import com.cloudproject.TeamC.CampEat.dto.request.UserJoinRequest;
import com.cloudproject.TeamC.CampEat.exception.CampEatException;
import com.cloudproject.TeamC.CampEat.exception.code.CampEatErrorCode;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.SchoolRepository;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.UserRepository;
import com.cloudproject.TeamC.global.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SchoolRepository schoolRepository;


    public void registerUser(UserJoinRequest userJoinRequestDto) {
        if (userRepository.findByEmail(userJoinRequestDto.getEmail()).isPresent()) {
            throw new CampEatException(CampEatErrorCode.USER_ALREADY_EXISTS);
        }

        School school = schoolRepository.findById(userJoinRequestDto.getSchoolId())
                .orElseThrow(() -> new CampEatException(CampEatErrorCode.SCHOOL_NOT_FOUND));

        User user = userJoinRequestDto.toEntity(school, passwordEncoder);
        userRepository.save(user);
        log.info("[AUTH] 사용자 저장", user.getId());
    }

    public String login(String email, String userPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CampEatException(CampEatErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(userPassword, user.getPassword())) {
            throw new CampEatException(CampEatErrorCode.INVALID_PASSWORD);
        }

        log.info("[AUTH] 해당 유저 찾기 성공", user.getId());
        return jwtTokenProvider.createToken(email);
    }
}