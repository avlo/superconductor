package com.prosilion.superconductor.base.repository.join.generic;

import com.prosilion.superconductor.base.entity.join.generic.GenericTagEntityElementAttributeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenericTagEntityElementAttributeEntityRepository extends JpaRepository<GenericTagEntityElementAttributeEntity, Long> {
  List<GenericTagEntityElementAttributeEntity> getAllByGenericTagId(Long id);
}
