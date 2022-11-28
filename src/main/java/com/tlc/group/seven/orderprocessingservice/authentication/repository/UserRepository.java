package com.tlc.group.seven.orderprocessingservice.authentication.repository;

import com.tlc.group.seven.orderprocessingservice.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String name);
    Boolean existsByName(String name);
    Boolean existsByEmail(String email);
}
