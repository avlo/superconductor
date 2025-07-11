package com.prosilion.superconductor.entity.join.generic;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@RedisHash("generic_tag-element_attribute-join")
public class GenericTagEntityElementAttributeEntity implements Serializable {
  @Id
  private Long id;
  private Long genericTagId;
  private Long elementAttributeId;

  public GenericTagEntityElementAttributeEntity(Long genericTagId, Long elementAttributeId) {
    this.genericTagId = genericTagId;
    this.elementAttributeId = elementAttributeId;
  }
}