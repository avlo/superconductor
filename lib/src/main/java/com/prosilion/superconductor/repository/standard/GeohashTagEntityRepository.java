package com.prosilion.superconductor.repository.standard;

import com.prosilion.superconductor.entity.standard.GeohashTagEntity;
import com.prosilion.superconductor.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeohashTagEntityRepository<T extends GeohashTagEntity> extends AbstractTagEntityRepository<T> {
}