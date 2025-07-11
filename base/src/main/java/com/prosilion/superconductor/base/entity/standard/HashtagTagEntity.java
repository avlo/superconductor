package com.prosilion.superconductor.base.entity.standard;

import com.prosilion.superconductor.base.dto.AbstractTagDto;
import com.prosilion.superconductor.base.dto.standard.HashtagTagDto;
import com.prosilion.superconductor.base.entity.AbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import lombok.Setter;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.HashtagTag;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "hashtag_tag")
public class HashtagTagEntity extends AbstractTagEntity {
  private String hashtagTag;

  public HashtagTagEntity(@NonNull HashtagTag hashtagTag) {
    super("t");
    this.hashtagTag = hashtagTag.getHashTag();
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

  @Override
  @Transient
  public List<String> get() {
    return List.of(hashtagTag);
  }
}
