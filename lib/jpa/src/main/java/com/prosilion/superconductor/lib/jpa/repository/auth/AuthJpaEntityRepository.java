package com.prosilion.superconductor.lib.jpa.repository.auth;

import com.prosilion.superconductor.lib.jpa.entity.auth.AuthJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthJpaEntityRepository extends JpaRepository<AuthJpaEntity, Long> {
  Optional<AuthJpaEntity> findBySessionId(String sessionId);
}
