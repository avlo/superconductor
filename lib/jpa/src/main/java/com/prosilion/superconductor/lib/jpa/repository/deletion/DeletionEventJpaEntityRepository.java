package com.prosilion.superconductor.lib.jpa.repository.deletion;

import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventJpaEntityIF;
import java.util.Collections;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface DeletionEventJpaEntityRepository extends JpaRepository<DeletionEventJpaEntity, Long> {
  @NonNull
  List<DeletionEventJpaEntity> findAll();
  default @NonNull List<DeletionEventJpaEntityIF> findAllEntities() {
    return Collections.unmodifiableList(findAll());
  }
}
