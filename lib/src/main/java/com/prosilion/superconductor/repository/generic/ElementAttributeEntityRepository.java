package com.prosilion.superconductor.repository.generic;

import com.prosilion.superconductor.entity.generic.ElementAttributeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElementAttributeEntityRepository extends JpaRepository<ElementAttributeEntity, Long> {
}