package com.prosilion.superconductor.lib.redis.repository;

import com.prosilion.superconductor.lib.redis.document.DeletionEventDocument;
import com.prosilion.superconductor.lib.redis.document.DeletionEventDocumentIF;
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

  default Optional<DeletionEventDocumentIF> findByDocumentEventId(@NonNull String eventIdString) {
    return findByEventId(eventIdString).map(DeletionEventDocumentIF.class::cast);
  }

  default @NonNull List<DeletionEventDocumentIF> findAllDocuments() {
    return Collections.unmodifiableList(findAll());
  }
}
