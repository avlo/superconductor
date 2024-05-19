package com.prosilion.superconductor.entity.classified;

import com.prosilion.superconductor.dto.RelaysTagDto;
import com.prosilion.superconductor.dto.ZapRequestDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import nostr.base.PublicKey;

@Data
@NoArgsConstructor
@Entity
@Table(name = "zaprequest")
public class ZapRequestEventEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer amount;
  private String lnUrl;
  private PublicKey recipientPubKey;

  @Transient
  private RelaysTagDto relaysTagDto;

  public ZapRequestEventEntity(Integer amount, String lnUrl, PublicKey recipientPubKey, RelaysTagDto relaysTagDto) {
    this.amount = amount;
    this.lnUrl = lnUrl;
    this.recipientPubKey = recipientPubKey;
    this.relaysTagDto = relaysTagDto;
  }

  public ZapRequestDto convertEntityToDto() {
    return new ZapRequestDto(relaysTagDto, amount, lnUrl, recipientPubKey);
  }
}
