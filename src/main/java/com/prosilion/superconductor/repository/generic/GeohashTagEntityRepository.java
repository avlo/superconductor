package com.prosilion.superconductor.repository.generic;

import com.prosilion.superconductor.entity.GeohashTagEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface GeohashTagEntityRepository<T extends GeohashTagEntity> extends GenericTagEntityRepository<T> {
}