package com.prosilion.superconductor.service.event.join.generic;

import com.prosilion.superconductor.dto.generic.ElementAttributeDto;
import com.prosilion.superconductor.entity.generic.ElementAttributeEntity;
import com.prosilion.superconductor.entity.join.generic.GenericTagEntityElementAttributeEntity;
import com.prosilion.superconductor.repository.generic.ElementAttributeEntityRepository;
import com.prosilion.superconductor.repository.join.generic.GenericTagEntityElementAttributeEntityRepository;
import jakarta.transaction.Transactional;
import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
public class GenericTagEntityElementAttributeEntityService {
  private final ElementAttributeEntityRepository repo;
  private final GenericTagEntityElementAttributeEntityRepository join;

  @Autowired
  public GenericTagEntityElementAttributeEntityService(
      ElementAttributeEntityRepository repo,
      GenericTagEntityElementAttributeEntityRepository join) {
    this.repo = repo;
    this.join = join;
  }

  public List<ElementAttributeDto> getElementAttributeList(@NonNull Long genericTagId) {
    return repo.findAllById(
            join.getAllByGenericTagId(genericTagId).stream()
                .map(GenericTagEntityElementAttributeEntity::getElementAttributeId).toList())
        .stream().map(ElementAttributeEntity::convertToDto).toList();
  }

  public void saveElementAttributeList(@NonNull Long genericTagId, @NonNull List<ElementAttributeDto> entities) {
    entities.stream().map(
            this::saveElementAttribute)
        .forEach(elementAttributeId ->
            saveJoins(genericTagId, elementAttributeId));
  }

  private ElementAttributeEntity saveElementAttribute(ElementAttributeDto elementAttributeEntity) {
    return repo.save(elementAttributeEntity.convertDtoToEntity());
  }

  private void saveJoins(Long genericTagId, ElementAttributeEntity elementAttributeEntity) {
    join.save(
        new GenericTagEntityElementAttributeEntity(
            genericTagId,
            elementAttributeEntity.getId()));
  }
}