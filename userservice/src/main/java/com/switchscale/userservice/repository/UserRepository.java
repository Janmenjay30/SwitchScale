package com.switchscale.userservice.repository;

import java.util.Optional;
import com.switchscale.userservice.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByEmail(String email);
    Optional<UserModel> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
