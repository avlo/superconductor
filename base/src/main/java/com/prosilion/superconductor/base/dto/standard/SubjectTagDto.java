package com.prosilion.superconductor.base.dto.standard;

import com.prosilion.superconductor.base.dto.AbstractTagDto;
import com.prosilion.superconductor.base.entity.standard.SubjectTagEntity;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.SubjectTag;

public class SubjectTagDto implements AbstractTagDto {
  private final SubjectTag subjectTag;

  public SubjectTagDto(@NonNull SubjectTag subjectTag) {
    this.subjectTag = subjectTag;
  }

  @Override
  public String getCode() {
    return subjectTag.getCode();
  }

  @Override
  public SubjectTagEntity convertDtoToEntity() {
    return new SubjectTagEntity(limit80(subjectTag.getSubject()));
  }

  private String limit80(String string) {
    return string.chars().limit(80).collect(
        StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
  }
}
