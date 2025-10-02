package com.prosilion.superconductor.lib.jpa.repository.standard;

import com.prosilion.superconductor.lib.jpa.entity.standard.GeohashTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagJpaEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeohashTagJpaEntityRepository<T extends GeohashTagJpaEntity> extends AbstractTagJpaEntityRepository<T> {
}
