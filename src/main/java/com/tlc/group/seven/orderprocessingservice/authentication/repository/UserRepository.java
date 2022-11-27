package com.tlc.group.seven.orderprocessingservice.authentication.repository;

import com.tlc.group.seven.orderprocessingservice.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

}
