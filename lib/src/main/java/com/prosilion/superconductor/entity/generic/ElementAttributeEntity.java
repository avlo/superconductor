package com.prosilion.superconductor.entity.generic;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.prosilion.nostr.event.internal.ElementAttribute;
import com.prosilion.superconductor.dto.generic.ElementAttributeDto;






import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@RedisHash("element_attribute")
public class ElementAttributeEntity implements Serializable {
  @Id
  private Long id;
  private String name;

  private String value;

  public ElementAttributeDto convertToDto() {
    return new ElementAttributeDto(new ElementAttribute(name, value));
  }
}
