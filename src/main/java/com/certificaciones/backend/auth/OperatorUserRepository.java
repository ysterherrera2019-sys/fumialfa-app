package com.certificaciones.backend.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperatorUserRepository extends JpaRepository<OperatorUser, Long> {

    Optional<OperatorUser> findByUsername(String username);
}
