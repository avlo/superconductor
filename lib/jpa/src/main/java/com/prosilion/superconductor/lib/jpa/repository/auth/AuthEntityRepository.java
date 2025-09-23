package com.prosilion.superconductor.lib.jpa.repository.auth;

import com.prosilion.superconductor.lib.jpa.entity.auth.AuthEntity;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(
    name = "superconductor.auth.active",
    havingValue = "true")
public interface AuthEntityRepository extends JpaRepository<AuthEntity, Long> {
  Optional<AuthEntity> findBySessionId(String sessionId);
}
