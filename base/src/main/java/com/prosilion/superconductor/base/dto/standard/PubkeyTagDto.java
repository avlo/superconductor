package com.prosilion.superconductor.base.dto.standard;

import com.prosilion.superconductor.base.dto.AbstractTagDto;
import com.prosilion.superconductor.base.entity.standard.PubkeyTagEntity;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.PubKeyTag;

public class PubkeyTagDto implements AbstractTagDto {
  private final PubKeyTag pubKeyTag;

  public PubkeyTagDto(@NonNull PubKeyTag pubKeyTag) {
    this.pubKeyTag = pubKeyTag;
  }

  @Override
  public String getCode() {
    return pubKeyTag.getCode();
  }

  @Override
  public PubkeyTagEntity convertDtoToEntity() {
    return new PubkeyTagEntity(pubKeyTag);
  }
}
