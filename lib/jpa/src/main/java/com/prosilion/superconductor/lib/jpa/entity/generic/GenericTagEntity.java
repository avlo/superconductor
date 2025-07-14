package com.prosilion.superconductor.lib.jpa.entity.generic;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import com.prosilion.superconductor.lib.jpa.dto.generic.ElementAttributeDto;
import com.prosilion.superconductor.lib.jpa.dto.generic.GenericTagDto;

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
