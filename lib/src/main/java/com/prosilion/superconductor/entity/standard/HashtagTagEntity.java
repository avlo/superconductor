package com.prosilion.superconductor.entity.standard;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.dto.standard.HashtagTagDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;



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
@RedisHash("hashtag_tag")
public class HashtagTagEntity extends AbstractTagEntity {
  private String hashtagTag;

  public HashtagTagEntity(@NonNull HashtagTag hashtagTag) {
    super("t");
    this.hashtagTag = hashtagTag.getHashTag();
  }

  @Override
  @org.springframework.data.annotation.Transient
  public BaseTag getAsBaseTag() {
    return new HashtagTag(hashtagTag);
  }

  @Override
  public AbstractTagDto convertEntityToDto() {
    return new HashtagTagDto(new HashtagTag(hashtagTag));
  }

  @Override
  @org.springframework.data.annotation.Transient
  public List<String> get() {
    return List.of(hashtagTag);
  }
}
