package com.prosilion.superconductor.base.repository.classified;

import com.prosilion.superconductor.base.entity.classified.PriceTagEntity;
import com.prosilion.superconductor.base.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceTagEntityRepository<T extends PriceTagEntity> extends AbstractTagEntityRepository<T> {
}
