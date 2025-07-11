package com.prosilion.superconductor.entity.standard;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.dto.standard.IdentifierTagDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;



import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import lombok.Setter;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.IdentifierTag;

@Setter
@Getter
@NoArgsConstructor
@RedisHash("identifier_tag")
public class IdentifierTagEntity extends AbstractTagEntity {
  private String uuid;

  public IdentifierTagEntity(@NonNull IdentifierTag identifierTag) {
    super("d");
    this.uuid = identifierTag.getUuid();
  }

  @Override
  @org.springframework.data.annotation.Transient
  public BaseTag getAsBaseTag() {
    return new IdentifierTag(uuid);
  }

  @Override
  public AbstractTagDto convertEntityToDto() {
    return new IdentifierTagDto(new IdentifierTag(uuid));
  }

  @Override
  @org.springframework.data.annotation.Transient
  public List<String> get() {
    return List.of(uuid);
  }
}
