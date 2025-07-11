package com.prosilion.superconductor.entity.generic;

import com.prosilion.superconductor.dto.generic.GenericTagDto;
import com.prosilion.superconductor.dto.generic.ElementAttributeDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@RedisHash("generic_tag")
public class GenericTagEntity implements Serializable {
  @Id
  private Long id;
  private String code;

  public GenericTagEntity(@NonNull String code) {
    this.code = code;
  }

  public GenericTagDto convertEntityToDto(List<ElementAttributeDto> atts) {
    return new GenericTagDto(code, atts);
  }
}
