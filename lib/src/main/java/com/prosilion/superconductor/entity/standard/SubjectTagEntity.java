package com.prosilion.superconductor.entity.standard;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.prosilion.superconductor.dto.standard.SubjectTagDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;



import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import lombok.Setter;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.SubjectTag;

@Setter
@Getter
@NoArgsConstructor
@RedisHash("subject_tag")
public class SubjectTagEntity extends AbstractTagEntity {
  private String subject;

  public SubjectTagEntity(@NonNull String subject) {
    super("subject");
    this.subject = subject;
  }

  @Override
  @org.springframework.data.annotation.Transient
  public BaseTag getAsBaseTag() {
    return new SubjectTag(subject);
  }

  @Override
  public SubjectTagDto convertEntityToDto() {
    return new SubjectTagDto(new SubjectTag(subject));
  }

  @Override
  @org.springframework.data.annotation.Transient
  public List<String> get() {
    return List.of(subject);
  }
}
