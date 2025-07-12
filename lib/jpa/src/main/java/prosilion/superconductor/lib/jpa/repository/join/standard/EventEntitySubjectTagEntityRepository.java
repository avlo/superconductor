package prosilion.superconductor.lib.jpa.repository.join.standard;

import prosilion.superconductor.lib.jpa.entity.join.standard.EventEntitySubjectTagEntity;
import prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntitySubjectTagEntityRepository<T extends EventEntitySubjectTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "subject";
  }
}
