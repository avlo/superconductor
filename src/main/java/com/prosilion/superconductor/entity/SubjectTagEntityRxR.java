package com.prosilion.superconductor.entity;

import com.prosilion.superconductor.dto.SubjectTagDtoRxR;
import com.prosilion.superconductor.entity.standard.StandardTagEntityRxR;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nostr.event.BaseTag;
import nostr.event.tag.SubjectTag;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "subject_tag")
public class SubjectTagEntityRxR extends StandardTagEntityRxR {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String subject;

  public SubjectTagEntityRxR(String subject) {
    this.subject = subject;
  }

  public BaseTag getAsBaseTag() {
    return new SubjectTag(subject);
  }

  @Override
  public String getCode() {
    return "subject";
  }
  public SubjectTagDtoRxR convertEntityToDto() {
    return new SubjectTagDtoRxR(subject);
  }
}
