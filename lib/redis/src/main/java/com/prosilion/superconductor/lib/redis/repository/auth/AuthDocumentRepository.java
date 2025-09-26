package com.prosilion.superconductor.lib.redis.repository.auth;

import com.prosilion.superconductor.lib.redis.document.AuthDocument;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnExpression("${superconductor.auth.req.active:true} || ${superconductor.auth.event.active:true}")
public interface AuthDocumentRepository extends ListCrudRepository<AuthDocument, String> {
  Optional<AuthDocument> findBySessionId(String sessionId);
}
