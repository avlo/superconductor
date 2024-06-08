package com.prosilion.superconductor.entity.generic;

import com.prosilion.superconductor.dto.GenericTagDto;
import com.prosilion.superconductor.dto.HashtagTagDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nostr.event.BaseTag;
import nostr.event.tag.HashtagTag;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "hashtag_tag")
public class HashtagTagEntity extends GenericTagEntity {
//  TODO: below annotations and id necessary for compilation even thuogh same is defined in GenericTagEntity
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String hashTag;

  public HashtagTagEntity(Character code, String hashTag) {
    super.setCode(code);
    this.hashTag = hashTag;
  }

  @Override
  public BaseTag getAsBaseTag() {
    return new HashtagTag(hashTag);
  }

  public GenericTagDto convertEntityToDto() {
    return new HashtagTagDto(hashTag);
  }
}
