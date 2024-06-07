package com.prosilion.superconductor.entity.standard;

import com.prosilion.superconductor.dto.StandardTagDto;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@MappedSuperclass
public abstract class StandardTagEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public abstract Character getCode();
  public abstract StandardTagDto convertEntityToDto();
}
