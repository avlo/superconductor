package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.SubjectTagEntity;
import lombok.Getter;
import nostr.event.tag.SubjectTag;

@Getter
public class SubjectTagDto {
  private final SubjectTag subjectTag;

  public SubjectTagDto(String subject) {
    subjectTag = SubjectTag.builder().subject(limit80(subject)).build();
  }

  private String limit80(String string) {
    return string.chars().limit(80).collect(
        StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
  }

  public SubjectTagEntity convertDtoToEntity() {
    return new SubjectTagEntity(limit80(subjectTag.getSubject()));
  }
}
