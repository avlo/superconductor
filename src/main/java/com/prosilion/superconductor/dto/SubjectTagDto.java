package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.SubjectTagEntity;
import lombok.Getter;
import lombok.Setter;
import nostr.event.tag.SubjectTag;

@Setter
@Getter
public class SubjectTagDto {
  private final SubjectTag subjectTag;

  public SubjectTagDto(String subject) {
    subjectTag = SubjectTag.builder().subject(subject).build();
  }

  public SubjectTagEntity convertDtoToEntity() {
    return new SubjectTagEntity(subjectTag.getSubject());
  }
}
