package com.prosilion.superconductor.repository.deletion;

import com.prosilion.superconductor.entity.join.deletion.DeletionEventEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeletionEventEntityRepository extends CrudRepository<DeletionEventEntity, Long> {
}
