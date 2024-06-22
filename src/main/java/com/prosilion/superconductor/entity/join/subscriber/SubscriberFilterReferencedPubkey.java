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
@Table(name = "subscriber-filter_referenced_pubkey")
public class SubscriberFilterReferencedPubkey implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long filterId;
  private String referencedPubkey;

  public SubscriberFilterReferencedPubkey(Long filterId, String referencedPubkey) {
    this.filterId = filterId;
    this.referencedPubkey = referencedPubkey;
  }
}