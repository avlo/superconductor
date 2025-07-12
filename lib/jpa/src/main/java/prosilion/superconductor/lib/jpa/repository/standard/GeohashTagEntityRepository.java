package prosilion.superconductor.lib.jpa.repository.standard;

import prosilion.superconductor.lib.jpa.entity.standard.GeohashTagEntity;
import prosilion.superconductor.lib.jpa.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeohashTagEntityRepository<T extends GeohashTagEntity> extends AbstractTagEntityRepository<T> {
}
