package com.prosilion.superconductor.lib.jpa.dto.standard;

import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.superconductor.lib.jpa.dto.AbstractTagDto;
import com.prosilion.superconductor.lib.jpa.entity.standard.ExternalIdentityTagJpaEntity;
import org.springframework.lang.NonNull;

public class ExternalIdentityTagDto implements AbstractTagDto {
  private final ExternalIdentityTag externalIdentityTag;

  public ExternalIdentityTagDto(@NonNull ExternalIdentityTag externalIdentityTag) {
    this.externalIdentityTag = externalIdentityTag;
  }

  @Override
  public String getCode() {
    return externalIdentityTag.getCode();
  }

  @Override
  public ExternalIdentityTagJpaEntity convertDtoToJpaEntity() {
    return new ExternalIdentityTagJpaEntity(externalIdentityTag);
  }
}
