package com.prosilion.superconductor.lib.jpa.repository.join.generic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.prosilion.superconductor.lib.jpa.entity.join.generic.GenericTagEntityElementAttributeJpaEntity;

@Repository
public interface GenericTagEntityElementAttributeEntityRepository extends JpaRepository<GenericTagEntityElementAttributeJpaEntity, Long> {
  List<GenericTagEntityElementAttributeJpaEntity> getAllByGenericTagId(Long id);
}
