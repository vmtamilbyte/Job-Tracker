package com.jobtracker;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByUserIdOrderByIdDesc(Long userId);
    Optional<JobApplication> findByIdAndUserId(Long id, Long userId);
}
