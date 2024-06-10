package com.prosilion.superconductor.entity;

import com.prosilion.superconductor.dto.SubjectTagDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.BaseTag;
import nostr.event.tag.SubjectTag;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "subject_tag")
public class SubjectTagEntity extends AbstractTagEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String subject;

  public SubjectTagEntity(@NonNull String subject) {
    this.subject = subject;
  }

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
}
