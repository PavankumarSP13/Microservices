package com.pavan.auth_service.repository;

import com.pavan.auth_service.model.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserCredentials, Integer> {
    UserCredentials findByUsername(String useranme);
}
