package com.prosilion.superconductor.repository.auth;

import com.prosilion.superconductor.entity.auth.AuthEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@ConditionalOnProperty(
    name = "superconductor.auth.active",
    havingValue = "true")
public interface AuthEntityRepository extends JpaRepository<AuthEntity, Long> {
  Optional<AuthEntity> findBySessionId(String sessionId);
}