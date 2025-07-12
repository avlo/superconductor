package prosilion.superconductor.lib.jpa.repository.join.standard;

import prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityEventTagEntity;
import prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityEventTagEntityRepository<T extends EventEntityEventTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "e";
  }
}
