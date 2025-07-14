package com.prosilion.superconductor.lib.jpa.repository.classified;

import com.prosilion.superconductor.lib.jpa.entity.classified.PriceTagEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceTagEntityRepository<T extends PriceTagEntity> extends AbstractTagEntityRepository<T> {
}
