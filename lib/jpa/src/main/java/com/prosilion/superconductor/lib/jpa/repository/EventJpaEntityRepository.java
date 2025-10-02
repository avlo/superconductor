package com.prosilion.superconductor.lib.jpa.repository;

import com.prosilion.superconductor.lib.jpa.entity.EventJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.EventJpaEntityIF;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface EventJpaEntityRepository extends JpaRepository<EventJpaEntity, Long> {
  Sort DESC_SORT_CREATED_AT = Sort.by(Sort.Direction.DESC, "createdAt");

  @NonNull
  List<EventJpaEntity> findAll(@NonNull Sort sort);

  Optional<EventJpaEntityIF> findByUid(@NonNull Long uid);

  Optional<EventJpaEntityIF> findByEventId(String eventId);

  List<EventJpaEntityIF> findByPubKey(@NonNull String pubKey, Sort sort);

  List<EventJpaEntityIF> findByKind(@NonNull Integer kind, Sort sort);

  default @NonNull List<EventJpaEntity> findAll() {
    return findAll(DESC_SORT_CREATED_AT);
  }

  default @NonNull List<EventJpaEntityIF> findByKind(@NonNull Integer kind) {
    return findByKind(kind, DESC_SORT_CREATED_AT);
  }

  default @NonNull List<EventJpaEntityIF> findByPubKey(@NonNull String pubKey) {
    return findByPubKey(pubKey, DESC_SORT_CREATED_AT);
  }
}
