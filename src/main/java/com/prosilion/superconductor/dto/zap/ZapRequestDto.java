package com.prosilion.superconductor.dto.zap;

import com.prosilion.superconductor.dto.classified.RelaysTagDto;
import com.prosilion.superconductor.entity.zap.ZapRequestEventEntity;
import lombok.Getter;
import nostr.event.impl.ZapRequest;

public class ZapRequestDto extends ZapRequest {
  private final RelaysTagDto relaysTagDto;
  @Getter
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
