package com.prosilion.superconductor.entity.standard;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.dto.standard.GeohashTagDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.BaseTag;
import nostr.event.tag.GeohashTag;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "geohash_tag")
public class GeohashTagEntity extends AbstractTagEntity {
  private String location;

  public GeohashTagEntity(@NonNull GeohashTag geohashTag) {
    this.location = geohashTag.getLocation();
  }

  @Override
  public String getCode() {
    return "g";
  }

  @Override
  @Transient
  public BaseTag getAsBaseTag() {
    return new GeohashTag(location);
  }

  @Override
  public AbstractTagDto convertEntityToDto() {
    return new GeohashTagDto(new GeohashTag(location));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GeohashTagEntity that = (GeohashTagEntity) o;
    return Objects.equals(location, that.location);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(location);
  }
}
