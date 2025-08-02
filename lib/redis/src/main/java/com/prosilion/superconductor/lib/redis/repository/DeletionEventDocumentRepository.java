package com.prosilion.superconductor.lib.redis.repository;

import com.prosilion.superconductor.lib.redis.document.DeletionEventDocument;
import com.prosilion.superconductor.lib.redis.document.DeletionEventDocumentRedisIF;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
// TODO: candidate replace below extends with CrudRepository for iterable streaming...
public interface DeletionEventDocumentRepository extends ListCrudRepository<DeletionEventDocument, String> {
  Sort DESC_SORT_CREATED_AT = Sort.by(Sort.Direction.DESC, "createdAt");

  @NonNull
  List<DeletionEventDocument> findAll(Sort sort);

  Optional<DeletionEventDocument> findByEventId(@NonNull String eventIdString);

  default Optional<DeletionEventDocumentRedisIF> findByDocumentEventId(@NonNull String eventIdString) {
    Optional<DeletionEventDocument> byEventId = findByEventId(eventIdString);
    Optional<DeletionEventDocumentRedisIF> deletionEventDocumentRedisIF = Optional.of(byEventId.map(DeletionEventDocumentRedisIF.class::cast)).orElse(Optional.empty());
    return deletionEventDocumentRedisIF;
  }

  default @NonNull List<DeletionEventDocumentRedisIF> findAllDocuments() {
    List<DeletionEventDocument> all = findAll(DESC_SORT_CREATED_AT);
    List<DeletionEventDocumentRedisIF> eventDocuments = Collections.unmodifiableList(all);
    return eventDocuments;
  }
}
