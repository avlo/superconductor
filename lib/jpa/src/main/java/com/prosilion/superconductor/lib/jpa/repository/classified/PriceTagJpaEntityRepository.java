package com.prosilion.superconductor.lib.jpa.repository.classified;

import com.prosilion.superconductor.lib.jpa.entity.classified.PriceTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagJpaEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceTagJpaEntityRepository<T extends PriceTagJpaEntity> extends AbstractTagJpaEntityRepository<T> {
}
