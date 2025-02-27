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

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "subject_tag")
public class SubjectTagEntity extends AbstractTagEntity {
  private String subject;
  private List<String> filterField;

  public SubjectTagEntity(@NonNull String subject) {
    super("subject");
    this.subject = subject;
    this.filterField = List.of(this.subject);
  }

  @Override
  @Transient
  public BaseTag getAsBaseTag() {
    return new SubjectTag(subject);
  }

  @Override
  public SubjectTagDto convertEntityToDto() {
    return new SubjectTagDto(new SubjectTag(subject));
  }
}
