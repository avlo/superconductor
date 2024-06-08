package com.prosilion.superconductor.dto.standard;

import com.prosilion.superconductor.entity.standard.PubkeyTagEntity;
import com.prosilion.superconductor.entity.standard.StandardTagEntity;
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
  public <T extends StandardTagEntity> T convertDtoToEntity() {
    return (T) new PubkeyTagEntity(pubKeyTag);
  }
}
