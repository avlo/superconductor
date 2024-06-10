package com.prosilion.superconductor.entity.standard;

import com.prosilion.superconductor.dto.AbstractTagDto;
import nostr.event.BaseTag;

import java.io.Serializable;

public interface Local extends Serializable {
  String getCode();
  AbstractTagDto convertEntityToDto();
  BaseTag getAsBaseTag();
}