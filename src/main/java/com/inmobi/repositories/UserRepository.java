package com.inmobi.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.inmobi.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
