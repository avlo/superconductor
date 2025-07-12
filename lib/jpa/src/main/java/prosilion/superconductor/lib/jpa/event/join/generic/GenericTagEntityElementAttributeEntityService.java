package prosilion.superconductor.lib.jpa.event.join.generic;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import prosilion.superconductor.lib.jpa.dto.generic.ElementAttributeDto;
import prosilion.superconductor.lib.jpa.entity.generic.ElementAttributeEntity;
import prosilion.superconductor.lib.jpa.entity.join.generic.GenericTagEntityElementAttributeEntity;
import prosilion.superconductor.lib.jpa.repository.generic.ElementAttributeEntityRepository;
import prosilion.superconductor.lib.jpa.repository.join.generic.GenericTagEntityElementAttributeEntityRepository;

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
