package com.prosilion.superconductor.entity.generic;

import com.prosilion.superconductor.dto.generic.GenericTagDto;
import com.prosilion.superconductor.dto.generic.GeohashTagDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nostr.event.BaseTag;
import nostr.event.tag.GeohashTag;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "geohash_tag")
public class GeohashTagEntity extends GenericTagEntity {
//  TODO: below annotations and id necessary for compilation even thuogh same is defined in GenericTagEntity
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String location;

  public GeohashTagEntity(Character code, String location) {
    super.setCode(code);
    this.location = location;
  }

  @Override
  public BaseTag getAsBaseTag() {
    return new GeohashTag(location);
  }

  public GenericTagDto convertEntityToDto() {
    return new GeohashTagDto(location);
  }
}
