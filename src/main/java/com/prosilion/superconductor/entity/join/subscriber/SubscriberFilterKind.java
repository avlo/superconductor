package com.prosilion.superconductor.entity.join.subscriber;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "subscriber-filter_kind-join")
public class SubscriberFilterKind implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long filterId;
  private Integer kindId;

  public SubscriberFilterKind(Long filterId, Integer kindId) {
    this.filterId = filterId;
    this.kindId = kindId;
  }
}