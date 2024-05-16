package com.prosilion.superconductor.repository.generic;

import com.prosilion.superconductor.entity.generic.GeohashTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeohashTagEntityRepository<T extends GeohashTagEntity> extends JpaRepository<T, Long>, GenericTagEntityRepository<T> {
}