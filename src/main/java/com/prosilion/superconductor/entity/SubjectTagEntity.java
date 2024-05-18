package com.prosilion.superconductor.entity;

import com.prosilion.superconductor.dto.SubjectTagDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "subject_tag")
public class SubjectTagEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String subject;

  public SubjectTagEntity(String subject) {
    this.subject = subject;
  }

  public SubjectTagDto convertEntityToDto() {
    return new SubjectTagDto(subject);
  }
}
