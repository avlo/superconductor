package com.prosilion.superconductor.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class GenericTagDto implements GenericTagDtoIF {
  private final String value;

  GenericTagDto(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return value;
  }
}
