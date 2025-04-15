package com.prosilion.superconductor.dto.generic;

import com.prosilion.superconductor.entity.generic.ElementAttributeEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nostr.base.ElementAttribute;

@Getter
@RequiredArgsConstructor
public class ElementAttributeDto {
  private final ElementAttribute elementAttribute;

  public ElementAttributeEntity convertDtoToEntity() {
    ElementAttributeEntity entity = new ElementAttributeEntity();
    entity.setName(elementAttribute.getName());
    entity.setValue(elementAttribute.getValue().toString());
    return entity;
  }
}
