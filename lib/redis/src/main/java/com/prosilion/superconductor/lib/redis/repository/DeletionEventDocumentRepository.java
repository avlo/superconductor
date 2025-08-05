package com.prosilion.superconductor.lib.redis.repository;

import com.prosilion.superconductor.lib.redis.document.DeletionEventDocument;
import com.prosilion.superconductor.lib.redis.document.DeletionEventDocumentRedisIF;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
// TODO: candidate replace below extends with CrudRepository for iterable streaming...
public interface DeletionEventDocumentRepository extends ListCrudRepository<DeletionEventDocument, String> {

  @NonNull
  List<DeletionEventDocument> findAll();

  Optional<DeletionEventDocument> findByEventId(@NonNull String eventIdString);

  default Optional<DeletionEventDocumentRedisIF> findByDocumentEventId(@NonNull String eventIdString) {
    return findByEventId(eventIdString).map(DeletionEventDocumentRedisIF.class::cast);
  }

  default @NonNull List<DeletionEventDocumentRedisIF> findAllDocuments() {
    return Collections.unmodifiableList(findAll());
  }
}
