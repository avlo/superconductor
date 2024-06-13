package com.prosilion.superconductor.repository.classified;

import com.prosilion.superconductor.entity.classified.PriceTagEntity;
import com.prosilion.superconductor.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceTagEntityRepository<T extends PriceTagEntity> extends AbstractTagEntityRepository<T> {
}
