package com.prosilion.superconductor.lib.redis.repository;

import com.prosilion.superconductor.lib.redis.document.DeletionEventDocumentRedisIF;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
// TODO: candidate replace below extends with CrudRepository for iterable streaming...
public interface DeletionEventDocumentRepository<T extends DeletionEventDocumentRedisIF> extends ListCrudRepository<T, String> {
  Sort DESC_SORT_CREATED_AT = Sort.by(Sort.Direction.DESC, "createdAt");

  @NonNull
  List<T> findAll(Sort sort);

  Optional<T> findByEventIdString(@NonNull String eventIdString);

  List<T> findAllByKind(Integer kind, Sort sort);

  default @NonNull List<T> findAll() {
    return findAll(DESC_SORT_CREATED_AT);
  }

  default @NonNull List<T> findAllByKind(@NonNull Integer kind) {
    return findAllByKind(kind, DESC_SORT_CREATED_AT);
  }
}
