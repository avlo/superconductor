package prosilion.superconductor.lib.jpa.repository.join.standard;

import prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityHashtagTagEntity;
import prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityHashtagTagEntityRepository<T extends EventEntityHashtagTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "t";
  }
}
