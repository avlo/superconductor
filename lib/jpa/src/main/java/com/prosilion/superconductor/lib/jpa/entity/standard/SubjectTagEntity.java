package com.prosilion.superconductor.lib.jpa.entity.standard;

import com.prosilion.superconductor.lib.jpa.dto.standard.SubjectTagDto;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import lombok.Setter;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.SubjectTag;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "subject_tag")
public class SubjectTagEntity extends AbstractTagEntity {
  private String subject;

  public SubjectTagEntity(@NonNull String subject) {
    super("subject");
    this.subject = subject;
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

  @Override
  @Transient
  public List<String> get() {
    return List.of(subject);
  }
}
