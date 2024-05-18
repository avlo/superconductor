package com.prosilion.superconductor.entity.generic;

import com.prosilion.superconductor.dto.GenericTagDto;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Setter
@Getter
@MappedSuperclass
public class GenericTagEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Character code;

  public GenericTagDto convertEntityToDto(GenericTagDto genericTagDto) {
    BeanUtils.copyProperties(this, genericTagDto, "code");
    return genericTagDto;
  }
}
