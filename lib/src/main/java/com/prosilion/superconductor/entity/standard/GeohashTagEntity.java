package com.prosilion.superconductor.entity.standard;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.dto.standard.GeohashTagDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;



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
@RedisHash("geohash_tag")
public class GeohashTagEntity extends AbstractTagEntity {
  private String location;

  public GeohashTagEntity(@NonNull GeohashTag geohashTag) {
    super("g");
    this.location = geohashTag.getLocation();
  }

  @Override
  @org.springframework.data.annotation.Transient
  public BaseTag getAsBaseTag() {
    return new GeohashTag(location);
  }

  @Override
  public AbstractTagDto convertEntityToDto() {
    return new GeohashTagDto(new GeohashTag(location));
  }

  @Override
  @org.springframework.data.annotation.Transient
  public List<String> get() {
    return List.of(location);
  }
}
