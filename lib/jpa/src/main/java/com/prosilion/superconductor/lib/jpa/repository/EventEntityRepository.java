package com.prosilion.superconductor.lib.jpa.repository;

import com.prosilion.superconductor.lib.jpa.entity.EventEntity;
import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityRepository extends JpaRepository<EventEntity, Long> {
  Sort DESC_SORT_CREATED_AT = Sort.by(Sort.Direction.DESC, "createdAt");

  @NonNull
  List<EventEntity> findAll(@NonNull Sort sort);

  Optional<EventEntityIF> findByUid(@NonNull Long uid);

  Optional<EventEntityIF> findByEventId(String eventId);

  List<EventEntityIF> findByPubKey(@NonNull String pubKey, Sort sort);

  List<EventEntityIF> findByKind(@NonNull Integer kind, Sort sort);

  default @NonNull List<EventEntity> findAll() {
    return findAll(DESC_SORT_CREATED_AT);
  }

  default @NonNull List<EventEntityIF> findByKind(@NonNull Integer kind) {
    return findByKind(kind, DESC_SORT_CREATED_AT);
  }

  default @NonNull List<EventEntityIF> findByPubKey(@NonNull String pubKey) {
    return findByPubKey(pubKey, DESC_SORT_CREATED_AT);
  }
}
