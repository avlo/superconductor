package com.prosilion.superconductor.dto.standard;

import com.prosilion.superconductor.entity.standard.PubkeyTagEntity;
import lombok.NonNull;
import nostr.event.tag.PubKeyTag;

public class PubkeyTagDto extends StandardTagDto implements StandardTagDtoIF {
  private final PubKeyTag pubKeyTag;

  public PubkeyTagDto(@NonNull PubKeyTag pubKeyTag) {
    this.pubKeyTag = pubKeyTag;
  }

  @Override
  public Character getCode() {
    return pubKeyTag.getCode().charAt(0);
  }

  @Override
  public PubkeyTagEntity convertDtoToEntity() {
    return new PubkeyTagEntity(pubKeyTag);
  }
}
