package com.prosilion.superconductor.lib.jpa.repository;

import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityRepository<T extends EventEntityIF> extends JpaRepository<T, Long> {
  Sort DESC_SORT_CREATED_AT = Sort.by(Sort.Direction.DESC, "createdAt");

//  @NonNull
//  List<EventEntity> findAll(@NonNull Sort sort);

  //  @NonNull
//  List<EventEntityIF> findAll(@NonNull Sort sort, Class<EventEntityIF> type);
  @NonNull
  List<T> findAll(@NonNull Sort sort, Class<T> type);

  @NonNull
  Optional<T> findById(@NonNull Long id);

  Optional<T> findByEventIdString(@NonNull String eventIdString);

  List<T> findByPubKey(@NonNull String pubKey, Sort sort);

  List<T> findByKind(@NonNull Integer kind, Sort sort);

  default @NonNull List<T> findAll(Class<T> type) {
    return findAll(DESC_SORT_CREATED_AT, type);
  }

  default @NonNull List<T> findByKind(@NonNull Integer kind) {
    return findByKind(kind, DESC_SORT_CREATED_AT);
  }

  default @NonNull List<T> findByPubKey(@NonNull String pubKey) {
    return findByPubKey(pubKey, DESC_SORT_CREATED_AT);
  }
}
