package com.prosilion.superconductor.repository.generic;

import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenericTagEntityRepository extends CrudRepository<GenericTagEntity, Long> {
}