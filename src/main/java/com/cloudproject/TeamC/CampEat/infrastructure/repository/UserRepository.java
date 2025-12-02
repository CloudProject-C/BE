package com.cloudproject.TeamC.CampEat.infrastructure.repository;

import com.cloudproject.TeamC.CampEat.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Long countBySchoolId(Long schoolId);

    Optional<User> findByEmail(String email);

}
