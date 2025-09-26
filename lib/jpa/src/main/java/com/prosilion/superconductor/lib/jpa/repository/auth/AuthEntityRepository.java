package com.prosilion.superconductor.lib.jpa.repository.auth;

import com.prosilion.superconductor.lib.jpa.entity.auth.AuthEntity;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnExpression("${superconductor.auth.req.active:true} || ${superconductor.auth.event.active:true}")
public interface AuthEntityRepository extends JpaRepository<AuthEntity, Long> {
  Optional<AuthEntity> findBySessionId(String sessionId);
}
