package prosilion.superconductor.lib.jpa.repository.join.standard;

import prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityGeohashTagEntity;
import prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityGeohashTagEntityRepository<T extends EventEntityGeohashTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "g";
  }
}
