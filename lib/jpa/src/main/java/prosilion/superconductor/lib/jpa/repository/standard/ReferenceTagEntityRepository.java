package prosilion.superconductor.lib.jpa.repository.standard;

import prosilion.superconductor.lib.jpa.entity.standard.ReferenceTagEntity;
import prosilion.superconductor.lib.jpa.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferenceTagEntityRepository<T extends ReferenceTagEntity> extends AbstractTagEntityRepository<T> {
}
