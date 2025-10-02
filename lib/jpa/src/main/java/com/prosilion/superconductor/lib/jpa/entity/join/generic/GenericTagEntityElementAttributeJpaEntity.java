package com.prosilion.superconductor.lib.jpa.entity.join.generic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "generic_tag-element_attribute-join")
public class GenericTagEntityElementAttributeJpaEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long genericTagId;
  private Long elementAttributeId;

  public GenericTagEntityElementAttributeJpaEntity(Long genericTagId, Long elementAttributeId) {
    this.genericTagId = genericTagId;
    this.elementAttributeId = elementAttributeId;
  }
}
