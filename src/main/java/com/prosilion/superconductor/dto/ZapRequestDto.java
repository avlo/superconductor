package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.ZapRequestEventEntity;
import lombok.Getter;
import lombok.Setter;
import nostr.event.impl.ZapRequestEvent;

@Setter
@Getter
public class ZapRequestDto extends ZapRequestEvent.ZapRequest {
  private final RelaysTagDto relaysTagDto;
  private final String recipientPubKey;
  private final Long amount;
  private final String lnUrl;

  public ZapRequestDto(String recipientPubKey, Long amount, String lnUrl, RelaysTagDto relaysTagDto) {
    super(relaysTagDto, amount, lnUrl);
    this.recipientPubKey = recipientPubKey;
    this.amount = amount;
    this.lnUrl = lnUrl;
    this.relaysTagDto = relaysTagDto;
  }

  public ZapRequestEventEntity convertDtoToEntity() {
    return new ZapRequestEventEntity(recipientPubKey, amount, lnUrl, relaysTagDto);
  }
}
