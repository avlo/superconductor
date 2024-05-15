package com.prosilion.superconductor.entity;

import com.prosilion.superconductor.dto.GenericTagDto;
import com.prosilion.superconductor.dto.GeohashTagDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "geohash_tag")
public class GeohashTagEntity implements GenericTagEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String code;
  private String location;

  public GeohashTagEntity(String location) {
    this.location = location;
  }

  public GenericTagDto convertEntityToDto() {
    GenericTagDto geohashTagDto = new GeohashTagDto(location);
    BeanUtils.copyProperties(this, geohashTagDto);
    return geohashTagDto;
  }
}
