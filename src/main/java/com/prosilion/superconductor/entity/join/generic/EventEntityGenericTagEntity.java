package com.prosilion.superconductor.entity.join.generic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

public interface EventEntityGenericTagEntity extends Serializable {
  Long getId();
  Long getEventId();
}