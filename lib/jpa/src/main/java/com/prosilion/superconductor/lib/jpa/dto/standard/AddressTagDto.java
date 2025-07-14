package com.prosilion.superconductor.lib.jpa.dto.standard;

import com.prosilion.superconductor.lib.jpa.dto.AbstractTagDto;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.lib.jpa.entity.standard.AddressTagEntity;

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
