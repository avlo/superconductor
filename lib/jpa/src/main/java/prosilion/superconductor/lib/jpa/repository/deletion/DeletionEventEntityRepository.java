package prosilion.superconductor.lib.jpa.repository.deletion;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventEntity;

@Repository
public interface DeletionEventEntityRepository extends JpaRepository<DeletionEventEntity, Long> {
  @NonNull List<DeletionEventEntity> findAll();
//  @NonNull List<DeletionEventEntityIF> findAll(Class<DeletionEventEntityIF> clazz);
}
