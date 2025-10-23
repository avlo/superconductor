package com.prosilion.superconductor.lib.redis.repository;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntity;
import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntityIF;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
// TODO: candidate replace below extends with CrudRepository for iterable streaming...
public interface EventNosqlEntityRepository extends ListCrudRepository<EventNosqlEntity, String> {
  Sort DESC_SORT_CREATED_AT = Sort.by(Sort.Direction.DESC, "createdAt");

  @NonNull
  List<EventNosqlEntity> findAll(@NonNull Sort sort);

  @NonNull
  Optional<EventNosqlEntity> findById(@NonNull String eventId);

  List<EventNosqlEntity> findByPubKey(@NonNull String pubKey, Sort sort);

  List<EventNosqlEntity> findByKind(@NonNull Integer kind, Sort sort);

  List<EventNosqlEntity> findByKindAndPubKey(@NonNull Integer kind, @NonNull String pubKey, Sort sort);

  default @NonNull List<EventNosqlEntityIF> findAllCustom() {
    return Collections.unmodifiableList(findAll(DESC_SORT_CREATED_AT));
  }

  default @NonNull Optional<EventNosqlEntityIF> findByEventId(String eventId) {
    return Optional.of(findById(eventId).map(EventNosqlEntityIF.class::cast)).orElse(Optional.empty());
  }

  default @NonNull List<EventNosqlEntityIF> findByKind(@NonNull Kind kind) {
    return Collections.unmodifiableList(findByKind(kind.getValue(), DESC_SORT_CREATED_AT));
  }

  default @NonNull List<EventNosqlEntityIF> findByAuthorPublicKey(@NonNull PublicKey authorPublicKey) {
    return Collections.unmodifiableList(findByPubKey(authorPublicKey.toHexString(), DESC_SORT_CREATED_AT));
  }

  default @NonNull List<EventNosqlEntityIF> findByKindAndAuthorPublicKey(@NonNull Kind kind, @NonNull PublicKey authorPublicKey) {
    return Collections.unmodifiableList(findByKindAndPubKey(kind.getValue(), authorPublicKey.toHexString(), DESC_SORT_CREATED_AT));
  }
}
