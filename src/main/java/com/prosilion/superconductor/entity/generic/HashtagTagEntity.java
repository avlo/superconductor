package com.prosilion.superconductor.entity.generic;

import com.prosilion.superconductor.dto.GenericTagDto;
import com.prosilion.superconductor.dto.HashtagTagDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "hashtag_tag")
public class HashtagTagEntity extends GenericTagEntity {
  private String hashTag;

  public HashtagTagEntity(Character code, String hashTag) {
    super.setCode(code);
    this.hashTag = hashTag;
  }

  public GenericTagDto convertEntityToDto() {
    return convertEntityToDto(new HashtagTagDto(hashTag));
  }
}
