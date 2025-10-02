package com.prosilion.superconductor.lib.jpa.repository.standard;

import com.prosilion.superconductor.lib.jpa.entity.standard.SubjectTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagJpaEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectTagJpaEntityRepository<T extends SubjectTagJpaEntity> extends AbstractTagJpaEntityRepository<T> {
}
