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

//  @NonNull
//  List<EventEntity> findAll(@NonNull Sort sort);

  //  @NonNull
//  List<EventEntityIF> findAll(@NonNull Sort sort, Class<EventEntityIF> type);
  @NonNull
  List<EventEntity> findAll(@NonNull Sort sort);

  @NonNull
  Optional<EventEntityIF> findById(@NonNull Long id, Sort sort, Class<EventEntityIF> type);

  Optional<EventEntityIF> findByEventIdString(@NonNull String eventIdString);

  List<EventEntityIF> findByPubKey(@NonNull String pubKey, Sort sort);

  List<EventEntityIF> findByKind(@NonNull Integer kind, Sort sort);

  default @NonNull List<EventEntity> findAll() {
    return findAll(DESC_SORT_CREATED_AT);
  }

  default @NonNull Optional<EventEntityIF> findByIdWoErasure(@NonNull Long id) {
    return findById(id, DESC_SORT_CREATED_AT, EventEntityIF.class);
  }

  default @NonNull List<EventEntityIF> findByKind(@NonNull Integer kind) {
    return findByKind(kind, DESC_SORT_CREATED_AT);
  }

  default @NonNull List<EventEntityIF> findByPubKey(@NonNull String pubKey) {
    return findByPubKey(pubKey, DESC_SORT_CREATED_AT);
  }

//  <S extends T> S save(S entity);
//  default @NonNull EventEntityIF save(EventEntityIF entity) {
//    return save(entity);
//  }
}
