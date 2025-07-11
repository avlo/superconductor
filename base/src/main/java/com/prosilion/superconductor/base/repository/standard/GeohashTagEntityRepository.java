package com.prosilion.superconductor.base.repository.standard;

import com.prosilion.superconductor.base.entity.standard.GeohashTagEntity;
import com.prosilion.superconductor.base.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeohashTagEntityRepository<T extends GeohashTagEntity> extends AbstractTagEntityRepository<T> {
}
