package prosilion.superconductor.lib.jpa.repository.join.standard;

import prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityReferenceTagEntity;
import prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityReferenceTagEntityRepository<T extends EventEntityReferenceTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "r";
  }
}
