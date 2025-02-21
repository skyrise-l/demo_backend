package com.zju.QueryArtisan.mysql;

import com.zju.QueryArtisan.entity.mysqlEntity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    Optional<User> findById(Long Id);

}