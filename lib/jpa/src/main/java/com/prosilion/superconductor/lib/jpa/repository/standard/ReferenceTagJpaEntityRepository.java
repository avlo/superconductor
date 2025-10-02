package com.prosilion.superconductor.lib.jpa.repository.standard;

import com.prosilion.superconductor.lib.jpa.entity.standard.ReferenceTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagJpaEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferenceTagJpaEntityRepository<T extends ReferenceTagJpaEntity> extends AbstractTagJpaEntityRepository<T> {
}
