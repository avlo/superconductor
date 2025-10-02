package com.prosilion.superconductor.lib.jpa.entity.standard;

import com.prosilion.superconductor.lib.jpa.dto.standard.SubjectTagDto;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagJpaEntity;
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
public class SubjectTagJpaEntity extends AbstractTagJpaEntity {
  private String subject;

  public SubjectTagJpaEntity(@NonNull String subject) {
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
