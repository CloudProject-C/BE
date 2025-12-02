package com.cloudproject.TeamC.CampEat.dto.request;

import com.cloudproject.TeamC.CampEat.domain.Gender;
import com.cloudproject.TeamC.CampEat.domain.School;
import com.cloudproject.TeamC.CampEat.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJoinRequest {
    private Long schoolId;

    // 이메일 (User.email)
    private String email;

    // 비밀번호 (User.password)
    private String password;

    // 닉네임 (User.nickname)
    private String nickname;

    // 프로필 이미지 URL (User.profileImage)
    private String profileImage;

    // 성별 (User.gender)
    private Gender gender;

    public User toEntity(School school, PasswordEncoder passwordEncoder) {
        return User.builder()
                .school(school)
                .email(this.email)
                .password(passwordEncoder.encode(this.password))
                .nickname(this.nickname)
                .profileImage(this.profileImage)
                .gender(this.gender)
                .build();
    }
}
