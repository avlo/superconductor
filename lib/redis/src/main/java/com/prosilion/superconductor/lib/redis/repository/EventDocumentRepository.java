package com.prosilion.superconductor.lib.redis.repository;

import com.prosilion.superconductor.lib.redis.document.EventDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
// TODO: candidate replace below extends with CrudRepository for iterable streaming...
public interface EventDocumentRepository extends ListCrudRepository<EventDocument, String> {
  Sort DESC_SORT_CREATED_AT = Sort.by(Sort.Direction.DESC, "createdAt");

  @NonNull List<EventDocument> findAll(Sort sort);
  Optional<EventDocument> findByEventIdString(@NonNull String eventIdString);
  List<EventDocument> findAllByKind(Integer kind, Sort sort);

  default @NonNull List<EventDocument> findAll() {
    return findAll(DESC_SORT_CREATED_AT);
  }

  default @NonNull List<EventDocument> findAllByKind(@NonNull Integer kind) {
    return findAllByKind(kind, DESC_SORT_CREATED_AT);
  }
}
