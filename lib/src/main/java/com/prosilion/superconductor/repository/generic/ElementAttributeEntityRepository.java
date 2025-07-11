package com.prosilion.superconductor.repository.generic;

import com.prosilion.superconductor.entity.generic.ElementAttributeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElementAttributeEntityRepository extends CrudRepository<ElementAttributeEntity, Long> {
}