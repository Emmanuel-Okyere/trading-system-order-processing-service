package com.tlc.group.seven.orderprocessingservice.authentication.repository;
import com.tlc.group.seven.orderprocessingservice.authentication.model.ERole;
import com.tlc.group.seven.orderprocessingservice.authentication.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository <Role, Long> {
    Optional<Role> findByName(ERole name);
}
