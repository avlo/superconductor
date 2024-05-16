package com.prosilion.superconductor.entity.generic;

import com.prosilion.superconductor.dto.GenericTagDto;
import com.prosilion.superconductor.dto.GeohashTagDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "geohash_tag")
public class GeohashTagEntity extends GenericTagEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String code;
  private String location;

  public GeohashTagEntity(String code, String location) {
    this.code = code;
    this.location = location;
  }

  public GenericTagDto convertEntityToDto() {
    GenericTagDto geohashTagDto = new GeohashTagDto(location);
    BeanUtils.copyProperties(this, geohashTagDto);
    return geohashTagDto;
  }
}
