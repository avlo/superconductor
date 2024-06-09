package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.dto.standard.StandardTagDtoRxR;
import com.prosilion.superconductor.entity.SubjectTagEntityRxR;
import nostr.event.tag.SubjectTag;

public class SubjectTagDtoRxR extends StandardTagDtoRxR {
  private final SubjectTag subjectTag;

  public SubjectTagDtoRxR(String subject) {
    subjectTag = SubjectTag.builder().subject(limit80(subject)).build();
  }

  private String limit80(String string) {
    return string.chars().limit(80).collect(
        StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
  }

  public SubjectTagEntityRxR convertDtoToEntity() {
    return new SubjectTagEntityRxR(limit80(subjectTag.getSubject()));
  }

  @Override
  public String getCode() {
    return "subject";
  }
}
