package com.prosilion.superconductor.lib.jpa.repository.standard;

import com.prosilion.superconductor.lib.jpa.entity.standard.RelayTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagJpaEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelayTagJpaEntityRepository<T extends RelayTagJpaEntity> extends AbstractTagJpaEntityRepository<T> {
}
