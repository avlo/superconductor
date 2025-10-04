package com.prosilion.superconductor.lib.redis.repository.auth;

import com.prosilion.superconductor.lib.redis.entity.AuthNosqlEntity;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthNosqlEntityRepository extends ListCrudRepository<AuthNosqlEntity, String> {
  Optional<AuthNosqlEntity> findBySessionId(String sessionId);
}
