package com.prosilion.superconductor.dto.generic;

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
