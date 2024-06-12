package com.prosilion.superconductor.repository.join.generic;

import com.prosilion.superconductor.entity.join.generic.EventEntityGenericTagEntity;
import com.prosilion.superconductor.entity.join.generic.GenericTagEntityElementAttributeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenericTagEntityElementAttributeEntityRepository extends JpaRepository<GenericTagEntityElementAttributeEntity, Long> {
  List<GenericTagEntityElementAttributeEntity> getAllByGenericTagId(Long id);
}