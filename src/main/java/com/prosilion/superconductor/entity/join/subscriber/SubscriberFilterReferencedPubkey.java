package com.prosilion.superconductor.entity.join.subscriber;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "subscriber-filter_referenced_pubkey")
public class SubscriberFilterReferencedPubkey extends AbstractSubscriberFilter {
  private String referencedPubkey;

  public SubscriberFilterReferencedPubkey(Long filterId, String referencedPubkey) {
    super(filterId);
    this.referencedPubkey = referencedPubkey;
  }
}