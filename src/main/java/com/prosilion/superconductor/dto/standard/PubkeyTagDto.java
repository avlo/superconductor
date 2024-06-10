package com.prosilion.superconductor.dto.standard;

import com.prosilion.superconductor.entity.standard.PubkeyTagEntity;
import lombok.NonNull;
import nostr.event.tag.PubKeyTag;

public class PubkeyTagDto implements StandardTagDto {
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
