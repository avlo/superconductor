package com.prosilion.superconductor.entity.standard;

import com.prosilion.superconductor.dto.standard.StandardTagDto;
import com.prosilion.superconductor.dto.standard.StandardTagDtoRxR;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import nostr.event.BaseTag;

import java.io.Serializable;

@Setter
@Getter
@MappedSuperclass
public abstract class StandardTagEntityRxR implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public abstract String getCode();
  public abstract StandardTagDtoRxR convertEntityToDto();
  public abstract BaseTag getAsBaseTag();
}
