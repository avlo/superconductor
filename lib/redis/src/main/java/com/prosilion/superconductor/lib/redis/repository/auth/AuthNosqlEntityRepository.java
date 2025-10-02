package com.prosilion.superconductor.lib.redis.repository.auth;

import com.prosilion.superconductor.lib.redis.document.AuthNosql;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnExpression("#{!'${superconductor.auth.event.kinds}'.isEmpty() || ${superconductor.auth.req.active:true}}")
public interface AuthNosqlEntityRepository extends ListCrudRepository<AuthNosql, String> {
  Optional<AuthNosql> findBySessionId(String sessionId);
}
