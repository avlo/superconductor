package com.prosilion.superconductor.entity;

import com.prosilion.superconductor.dto.AbstractTagDto;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import lombok.Setter;
import com.prosilion.nostr.tag.BaseTag;

@Setter
@Getter
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractTagEntity implements Supplier<List<String>>, Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String code;

  public AbstractTagEntity(@NonNull String code) {
    this.code = code;
  }

  public abstract AbstractTagDto convertEntityToDto();

  public abstract BaseTag getAsBaseTag();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractTagEntity that = (AbstractTagEntity) o;
    return Objects.equals(get(), that.get());
  }

  @Override
  public int hashCode() {
    return Objects.hash(get());
  }
}
