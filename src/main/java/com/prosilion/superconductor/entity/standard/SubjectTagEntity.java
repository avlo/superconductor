package com.prosilion.superconductor.entity.standard;

import com.prosilion.superconductor.dto.standard.SubjectTagDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.BaseTag;
import nostr.event.tag.SubjectTag;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "subject_tag")
public class SubjectTagEntity extends AbstractTagEntity {
  private String subject;

  public SubjectTagEntity(@NonNull String subject) {
    this.subject = subject;
  }

  @Override
  @Transient
  public BaseTag getAsBaseTag() {
    return new SubjectTag(subject);
  }

  @Override
  public String getCode() {
    return "subject";
  }

  @Override
  public SubjectTagDto convertEntityToDto() {
    return new SubjectTagDto(new SubjectTag(subject));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SubjectTagEntity that = (SubjectTagEntity) o;
    return Objects.equals(subject, that.subject);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(subject);
  }
}
