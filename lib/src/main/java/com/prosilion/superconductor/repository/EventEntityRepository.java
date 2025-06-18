package com.prosilion.superconductor.repository;

import com.prosilion.superconductor.entity.EventEntity;
import org.springframework.lang.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventEntityRepository extends JpaRepository<EventEntity, Long> {

  //  @Cacheable("events")
//  @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
  @NonNull
  List<EventEntity> findAll();

  //  @Cacheable("events")
//  @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
  List<EventEntity> findByContent(@NonNull String content);
  Optional<EventEntity> findByEventIdString(@NonNull String eventIdString);
  List<EventEntity> findByPubKey(@NonNull String pubKey);
  List<EventEntity> findByKind(Integer kind);
}
