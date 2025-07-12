package prosilion.superconductor.lib.jpa.repository.standard;

import prosilion.superconductor.lib.jpa.entity.standard.HashtagTagEntity;
import prosilion.superconductor.lib.jpa.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashtagTagEntityRepository<T extends HashtagTagEntity> extends AbstractTagEntityRepository<T> {
}
