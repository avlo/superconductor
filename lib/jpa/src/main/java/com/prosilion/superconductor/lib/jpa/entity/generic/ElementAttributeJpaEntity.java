package com.prosilion.superconductor.lib.jpa.entity.generic;

import com.prosilion.nostr.event.internal.ElementAttribute;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import com.prosilion.superconductor.lib.jpa.dto.generic.ElementAttributeDto;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "element_attribute")
public class ElementAttributeJpaEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;

  @Column(name = "\"value\"")
  private String value;

  public ElementAttributeDto convertToDto() {
    return new ElementAttributeDto(new ElementAttribute(name, value));
  }
}
