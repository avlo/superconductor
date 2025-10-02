package com.prosilion.superconductor.lib.jpa.repository.standard;

import com.prosilion.superconductor.lib.jpa.entity.standard.EventTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagJpaEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventTagJpaEntityRepository<T extends EventTagJpaEntity> extends AbstractTagJpaEntityRepository<T> {
}
