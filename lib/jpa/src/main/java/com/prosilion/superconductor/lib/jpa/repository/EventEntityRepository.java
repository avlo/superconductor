package com.prosilion.superconductor.lib.jpa.repository;

import com.prosilion.superconductor.lib.jpa.entity.EventEntity;
import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityRepository extends JpaRepository<EventEntity, Long> {
  @NonNull List<EventEntity> findAll();
  @NonNull Optional<EventEntity> findById(@NonNull Long id);

  Optional<EventEntity> findByEventIdString(@NonNull String eventIdString);
  List<EventEntityIF> findByPubKey(@NonNull String pubKey);
  List<EventEntityIF> findByKind(@NonNull Integer kind);
}
