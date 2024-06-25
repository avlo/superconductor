package com.prosilion.superconductor.entity.join.subscriber;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SubscriberFilterReferencedPubkey that = (SubscriberFilterReferencedPubkey) o;
    return Objects.equals(referencedPubkey, that.referencedPubkey);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(referencedPubkey);
  }
}