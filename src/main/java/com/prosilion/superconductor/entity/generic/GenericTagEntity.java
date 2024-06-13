package com.prosilion.superconductor.entity.generic;

import com.prosilion.superconductor.dto.generic.GenericTagDto;
import com.prosilion.superconductor.dto.generic.ElementAttributeDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "generic_tag")
public class GenericTagEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String code;

  public GenericTagEntity(@NonNull String code) {
    this.code = code;
  }

  public GenericTagDto convertEntityToDto(List<ElementAttributeDto> atts) {
    return new GenericTagDto(code, atts);
  }
}
