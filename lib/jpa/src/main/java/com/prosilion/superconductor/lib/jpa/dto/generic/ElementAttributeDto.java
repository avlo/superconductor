package com.prosilion.superconductor.lib.jpa.dto.generic;

import com.prosilion.nostr.event.internal.ElementAttribute;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.prosilion.superconductor.lib.jpa.entity.generic.ElementAttributeEntity;

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
