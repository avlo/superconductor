package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.classified.ZapRequestEventEntity;
import lombok.Getter;
import lombok.Setter;
import nostr.base.PublicKey;
import nostr.event.impl.ZapRequestEvent.ZapRequest;

@Setter
@Getter
public class ZapRequestDto extends ZapRequest {
  public ZapRequestDto(RelaysTagDto relaysTagDto, Integer amount, String lnUrl, PublicKey recipientPubKey) {
    super(relaysTagDto, amount, lnUrl, recipientPubKey);
  }

  public ZapRequestEventEntity convertDtoToEntity() {
    return new ZapRequestEventEntity(getAmount(), getLnUrl(), getRecipientPubKey(), new RelaysTagDto(getRelaysTag().getRelayUris()));
  }
}
