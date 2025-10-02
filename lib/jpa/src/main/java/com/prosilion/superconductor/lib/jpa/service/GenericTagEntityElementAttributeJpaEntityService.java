package com.prosilion.superconductor.lib.jpa.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import com.prosilion.superconductor.lib.jpa.dto.generic.ElementAttributeDto;
import com.prosilion.superconductor.lib.jpa.entity.generic.ElementAttributeJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.join.generic.GenericTagEntityElementAttributeJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.generic.ElementAttributeJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.join.generic.GenericTagEntityElementAttributeEntityRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Transactional
@Service
public class GenericTagEntityElementAttributeJpaEntityService {
  private final ElementAttributeJpaEntityRepository repo;
  private final GenericTagEntityElementAttributeEntityRepository join;

  @Autowired
  public GenericTagEntityElementAttributeJpaEntityService(
      @NonNull ElementAttributeJpaEntityRepository repo,
      @NonNull GenericTagEntityElementAttributeEntityRepository join) {
    this.repo = repo;
    this.join = join;
  }

  public List<ElementAttributeDto> getElementAttributeList(@NonNull Long genericTagId) {
    return repo.findAllById(
            join.getAllByGenericTagId(genericTagId).stream()
                .map(GenericTagEntityElementAttributeJpaEntity::getElementAttributeId).toList())
        .stream().map(ElementAttributeJpaEntity::convertToDto).toList();
  }

  public void saveElementAttributeList(@NonNull Long genericTagId, @NonNull List<ElementAttributeDto> entities) {
    entities.stream().map(
            this::saveElementAttribute)
        .forEach(elementAttributeId ->
            saveJoins(genericTagId, elementAttributeId));
  }

  private ElementAttributeJpaEntity saveElementAttribute(ElementAttributeDto elementAttributeEntity) {
    return repo.save(elementAttributeEntity.convertDtoToEntity());
  }

  private void saveJoins(Long genericTagId, ElementAttributeJpaEntity elementAttributeJpaEntity) {
    join.save(
        new GenericTagEntityElementAttributeJpaEntity(
            genericTagId,
            elementAttributeJpaEntity.getId()));
  }
}
