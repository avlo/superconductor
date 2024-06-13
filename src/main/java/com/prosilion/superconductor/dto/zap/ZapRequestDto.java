package com.prosilion.superconductor.dto.zap;

import com.prosilion.superconductor.dto.standard.RelaysTagDto;
import com.prosilion.superconductor.entity.zap.ZapRequestEventEntity;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.impl.ZapRequest;

@Setter
@Getter
public class ZapRequestDto extends ZapRequest {
  private final String recipientPubKey;
  private final Long amount;
  private final String lnUrl;

  public ZapRequestDto(@NonNull String recipientPubKey, @NonNull Long amount, @NonNull String lnUrl, @NonNull RelaysTagDto relaysTagDto) {
    super(relaysTagDto, amount, lnUrl);
    this.recipientPubKey = recipientPubKey;
    this.amount = amount;
    this.lnUrl = lnUrl;
  }

  public ZapRequestEventEntity convertDtoToEntity() {
    return new ZapRequestEventEntity(recipientPubKey, amount, lnUrl);
  }
}
