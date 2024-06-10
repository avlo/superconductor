package com.prosilion.superconductor.entity.generic;

import com.prosilion.superconductor.dto.generic.GeohashTagDto;
import com.prosilion.superconductor.dto.standard.StandardTagDto;
import com.prosilion.superconductor.entity.standard.AbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.BaseTag;
import nostr.event.tag.GeohashTag;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "geohash_tag")
public class GeohashTagEntity extends AbstractTagEntity {
  //  TODO: below annotations and id necessary for compilation even thuogh same is defined in GenericTagEntity
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String location;

  public GeohashTagEntity(@NonNull GeohashTag geohashTag) {
    this.location = geohashTag.getLocation();
  }

  @Override
  public BaseTag getAsBaseTag() {
    return new GeohashTag(location);
  }

  @Override
  public String getCode() {
    return "g";
  }

  @Override
  public StandardTagDto convertEntityToDto() {
    return new GeohashTagDto(new GeohashTag(location));
  }
}
