package com.prosilion.superconductor.base.dto.standard;

import com.prosilion.superconductor.base.dto.AbstractTagDto;
import com.prosilion.superconductor.base.entity.standard.AddressTagEntity;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.AddressTag;

public class AddressTagDto implements AbstractTagDto {
  private final AddressTag addressTag;

  public AddressTagDto(@NonNull AddressTag addressTag) {
    this.addressTag = addressTag;
  }

  @Override
  public String getCode() {
    return addressTag.getCode();
  }

  @Override
  public AddressTagEntity convertDtoToEntity() {
    return new AddressTagEntity(addressTag);
  }
}
