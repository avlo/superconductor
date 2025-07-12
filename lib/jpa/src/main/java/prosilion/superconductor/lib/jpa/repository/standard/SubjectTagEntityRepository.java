package prosilion.superconductor.lib.jpa.repository.standard;

import prosilion.superconductor.lib.jpa.entity.standard.SubjectTagEntity;
import prosilion.superconductor.lib.jpa.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectTagEntityRepository<T extends SubjectTagEntity> extends AbstractTagEntityRepository<T> {
}
