package com.prosilion.superconductor.entity.generic;

import com.prosilion.superconductor.dto.GenericTagDto;
import com.prosilion.superconductor.dto.GeohashTagDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "geohash_tag")
public class GeohashTagEntity extends GenericTagEntity {
  private String location;

  public GeohashTagEntity(String code, String location) {
    super.setCode(code);
    this.location = location;
  }

  public GenericTagDto convertEntityToDto() {
    return convertEntityToDto(new GeohashTagDto(location));
  }
}
