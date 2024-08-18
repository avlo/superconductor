package com.prosilion.superconductor.repository.auth;

import com.prosilion.superconductor.entity.auth.AuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthEntityRepository extends JpaRepository<AuthEntity, Long> {
  Optional<AuthEntity> findBySessionId(String sessionId);
}