package prosilion.superconductor.lib.jpa.repository.deletion;

import com.prosilion.superconductor.base.DeletionEventEntityIF;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventEntity;

@Repository
public interface DeletionEventEntityRepository extends JpaRepository<DeletionEventEntity, Long> {
//  List<DeletionEventEntityIF> findAll();

  @NonNull
  List<DeletionEventEntityIF> findAll(Class<DeletionEventEntityIF> clazz);
}
