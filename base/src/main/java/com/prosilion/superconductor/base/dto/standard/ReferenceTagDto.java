package com.prosilion.superconductor.base.dto.standard;

import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.superconductor.base.dto.AbstractTagDto;
import com.prosilion.superconductor.base.entity.standard.ReferenceTagEntity;
import lombok.Getter;
import org.springframework.lang.NonNull;

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
