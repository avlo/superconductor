package com.prosilion.superconductor.dto.standard;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.entity.standard.IdentifierTagEntity;
import lombok.NonNull;
import nostr.event.tag.IdentifierTag;

public class IdentifierTagDto implements AbstractTagDto {
  private final IdentifierTag identifierTag;

  public IdentifierTagDto(@NonNull IdentifierTag identifierTag) {
    this.identifierTag = identifierTag;
  }

  @Override
  public String getCode() {
    return identifierTag.getCode();
  }

  @Override
  public IdentifierTagEntity convertDtoToEntity() {
    return new IdentifierTagEntity(identifierTag);
  }
}
