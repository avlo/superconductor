package com.prosilion.superconductor.entity.standard;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.dto.standard.HashtagTagDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.BaseTag;
import nostr.event.tag.HashtagTag;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "hashtag_tag")
public class HashtagTagEntity extends AbstractTagEntity {
  private String hashtagTag;
  private List<String> filterField;

  public HashtagTagEntity(@NonNull HashtagTag hashtagTag) {
    super("t");
    this.hashtagTag = hashtagTag.getHashTag();
    this.filterField = List.of(this.hashtagTag);
  }

  @Override
  @Transient
  public BaseTag getAsBaseTag() {
    return new HashtagTag(hashtagTag);
  }

  @Override
  public AbstractTagDto convertEntityToDto() {
    return new HashtagTagDto(new HashtagTag(hashtagTag));
  }
}
