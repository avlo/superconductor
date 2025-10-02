package com.prosilion.superconductor.lib.jpa.entity.standard;

import com.prosilion.superconductor.lib.jpa.dto.AbstractTagDto;
import com.prosilion.superconductor.lib.jpa.dto.standard.GeohashTagDto;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import lombok.Setter;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.GeohashTag;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "geohash_tag")
public class GeohashTagJpaEntity extends AbstractTagJpaEntity {
  private String location;

  public GeohashTagJpaEntity(@NonNull GeohashTag geohashTag) {
    super("g");
    this.location = geohashTag.getLocation();
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
  @Transient
  public List<String> get() {
    return List.of(location);
  }
}
