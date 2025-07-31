package com.prosilion.superconductor.lib.redis.repository;

import com.prosilion.superconductor.lib.redis.document.EventDocument;
import com.prosilion.superconductor.lib.redis.document.EventDocumentIF;
import java.util.Collections;
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

  @NonNull
  List<EventDocument> findAll(@NonNull Sort sort);

  Optional<EventDocument> findByEventId(String eventId);

  List<EventDocument> findByPubKey(@NonNull String pubKey, Sort sort);

  List<EventDocument> findByKind(@NonNull Integer kind, Sort sort);

  default @NonNull List<EventDocumentIF> findAllCustom() {
    List<EventDocument> all = findAll(DESC_SORT_CREATED_AT);
    List<EventDocumentIF> eventDocuments = Collections.unmodifiableList(all);
    return eventDocuments;
  }

  default @NonNull Optional<EventDocumentIF> findByEventIdICustom(String eventId) {
    Optional<EventDocument> byEventId = findByEventId(eventId);
    Optional<EventDocumentIF> eventDocumentIF = Optional.of(byEventId).map(EventDocumentIF.class::cast);
    return eventDocumentIF;
  }
  
  default @NonNull List<EventDocumentIF> findByKind(@NonNull Integer kind) {
    List<EventDocument> byKind = findByKind(kind, DESC_SORT_CREATED_AT);
    List<EventDocumentIF> eventDocumentIFS = Collections.unmodifiableList(byKind);
    return eventDocumentIFS;
  }

  default @NonNull List<EventDocumentIF> findByPubKey(@NonNull String pubKey) {
    List<EventDocument> byPubKey = findByPubKey(pubKey, DESC_SORT_CREATED_AT);
    List<EventDocumentIF> eventDocumentIFS = Collections.unmodifiableList(byPubKey);
    return eventDocumentIFS;
  }
}
