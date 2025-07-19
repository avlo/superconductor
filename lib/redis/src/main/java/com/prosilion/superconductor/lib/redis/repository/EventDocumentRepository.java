package com.prosilion.superconductor.lib.redis.repository;

import com.prosilion.superconductor.lib.redis.document.EventDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
// TODO: candidate replace below extends with CrudRepository for iterable streaming...
public interface EventDocumentRepository extends ListCrudRepository<EventDocument, String> {
  Optional<EventDocument> findByEventIdString(@NonNull String eventIdString);
  List<EventDocument> findAllByKind(Integer kind);
}
