package com.cloudproject.TeamC.CampEat.dto.request;

import com.cloudproject.TeamC.CampEat.domain.Gender;
import com.cloudproject.TeamC.CampEat.domain.School;
import com.cloudproject.TeamC.CampEat.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회원가입 요청")
public class UserJoinRequest {

    @Schema(description = "학교 ID", example = "1")
    private Long schoolId;

    @Schema(description = "이메일", example = "test@khu.ac.kr")
    private String email;

    @Schema(description = "비밀번호", example = "password1234")
    private String password;

    @Schema(description = "닉네임", example = "쩝쩝박사")
    private String nickname;

    @Schema(description = "프로필 이미지 URL (선택)", example = "https://s3.../profile.jpg")
    private String profileImage;

    @Schema(description = "성별 (MALE, FEMALE)", example = "MALE")
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
