package com.prosilion.superconductor.lib.jpa.repository.auth;

import com.prosilion.superconductor.lib.jpa.entity.auth.AuthJpaEntity;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnExpression("#{!'${superconductor.auth.event.kinds}'.isEmpty() || ${superconductor.auth.req.active:true}}")
public interface AuthJpaEntityRepository extends JpaRepository<AuthJpaEntity, Long> {
  Optional<AuthJpaEntity> findBySessionId(String sessionId);
}
