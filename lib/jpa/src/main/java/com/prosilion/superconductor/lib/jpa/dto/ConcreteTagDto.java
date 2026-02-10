package com.prosilion.superconductor.lib.jpa.dto;

import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagJpaEntity;
import org.springframework.lang.NonNull;

import java.util.function.Function;

public class ConcreteTagDto<T extends BaseTag, E extends AbstractTagJpaEntity> implements AbstractTagDto {
  private final T tag;
  private final Function<T, E> entityFactory;

  public ConcreteTagDto(@NonNull T tag, @NonNull Function<T, E> entityFactory) {
    this.tag = tag;
    this.entityFactory = entityFactory;
  }

  @Override
  public String getCode() {
    return tag.getCode();
  }

  @Override
  public E convertDtoToJpaEntity() {
    return entityFactory.apply(tag);
  }
}
