package com.prosilion.superconductor.dto.standard;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.entity.standard.AddressTagEntity;
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
