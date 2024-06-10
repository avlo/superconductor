package com.prosilion.superconductor.entity.standard;

import com.prosilion.superconductor.dto.standard.StandardTagDto;
import nostr.event.BaseTag;

import java.io.Serializable;

public interface Local extends Serializable {
  String getCode();
  StandardTagDto convertEntityToDto();
  BaseTag getAsBaseTag();
}