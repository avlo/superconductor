package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.dto.standard.StandardTagDtoRxR;
import com.prosilion.superconductor.entity.SubjectTagEntityRxR;
import lombok.NonNull;
import nostr.event.tag.SubjectTag;

public class SubjectTagDtoRxR implements StandardTagDtoRxR {
  private final SubjectTag subjectTag;

  public SubjectTagDtoRxR(@NonNull SubjectTag subjectTag) {
    this.subjectTag = subjectTag;
  }

  @Override
  public String getCode() {
    return subjectTag.getCode();
  }

  @Override
  public SubjectTagEntityRxR convertDtoToEntity() {
    return new SubjectTagEntityRxR(limit80(subjectTag.getSubject()));
  }

  private String limit80(String string) {
    return string.chars().limit(80).collect(
        StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
  }
}
