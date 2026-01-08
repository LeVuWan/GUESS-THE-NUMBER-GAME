package com.inmobi.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.inmobi.dtos.res.GetTopUsersByScore;
import com.inmobi.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findTopByOrderByIdDesc();

    @Query("SELECT new  com.inmobi.dtos.res.GetTopUsersByScore(u.id, u.username, u.score) " +
            "FROM tbl_users u")
    Page<GetTopUsersByScore> findAllTopUsers(Pageable pageable);
}
