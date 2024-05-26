package com.prosilion.superconductor.entity;

import com.prosilion.superconductor.dto.RelaysTagDto;
import com.prosilion.superconductor.dto.ZapRequestDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import nostr.event.tag.RelaysTag;

@Data
@NoArgsConstructor
@Entity
@Table(name = "zaprequest")
public class ZapRequestEventEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long amount;
  private String lnUrl;
  private String recipientPubKey;

  @Transient
  private RelaysTagDto relaysTagDto;

  public ZapRequestEventEntity(String recipientPubKey, Long amount, String lnUrl, RelaysTagDto relaysTagDto) {
    this.recipientPubKey = recipientPubKey;
    this.amount = amount;
    this.lnUrl = lnUrl;
    this.relaysTagDto = relaysTagDto;
  }

  public ZapRequestDto convertEntityToDto() {
    return new ZapRequestDto(recipientPubKey, amount, lnUrl, relaysTagDto);
  }
}
