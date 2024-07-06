package com.prosilion.superconductor.entity.join.subscriber;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Component
public class SubscriberFilterReferencedPubkey extends AbstractFilterType {
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

  @Override
  public String getCode() {
    return "referencedPubKey";
  }
}