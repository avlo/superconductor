package com.prosilion.superconductor.lib.redis.repository;

import com.prosilion.superconductor.lib.redis.document.DeletionEventNosqlEntity;
import com.prosilion.superconductor.lib.redis.document.DeletionEventNosqlEntityIF;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
// TODO: candidate replace below extends with CrudRepository for iterable streaming...
public interface DeletionEventNosqlEntityRepository extends ListCrudRepository<DeletionEventNosqlEntity, String> {

  @NonNull
  List<DeletionEventNosqlEntity> findAll();

  Optional<DeletionEventNosqlEntity> findByEventId(@NonNull String eventIdString);

  default Optional<DeletionEventNosqlEntityIF> findByNosqlEntityEventId(@NonNull String eventIdString) {
    return findByEventId(eventIdString).map(DeletionEventNosqlEntityIF.class::cast);
  }

  default @NonNull List<DeletionEventNosqlEntityIF> findAllNosqlEntities() {
    return Collections.unmodifiableList(findAll());
  }
}
