package com.prosilion.superconductor.dto.standard;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.entity.standard.RelaysTagEntity;
import lombok.Getter;
import lombok.NonNull;
import nostr.event.tag.RelaysTag;

public class RelaysTagDto implements AbstractTagDto {
  @Getter
  private final RelaysTag relaysTag;

  public RelaysTagDto(@NonNull RelaysTag relaysTag) {
    this.relaysTag = relaysTag;
  }

  @Override
  public String getCode() {
    return relaysTag.getCode();
  }

  @Override
  public RelaysTagEntity convertDtoToEntity() {
    return new RelaysTagEntity(relaysTag);
  }
}
