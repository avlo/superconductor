package com.prosilion.superconductor.lib.jpa.dto.standard;

import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.superconductor.lib.jpa.dto.AbstractTagDto;
import lombok.Getter;
import org.springframework.lang.NonNull;
import com.prosilion.superconductor.lib.jpa.entity.standard.ReferenceTagEntity;

@Getter
public class ReferenceTagDto implements AbstractTagDto {
  private final ReferenceTag referenceTag;

  public ReferenceTagDto(@NonNull ReferenceTag referenceTag) {
    this.referenceTag = referenceTag;
  }

  @Override
  public String getCode() {
    return referenceTag.getCode();
  }

  @Override
  public ReferenceTagEntity convertDtoToEntity() {
    return new ReferenceTagEntity(referenceTag);
  }
}
