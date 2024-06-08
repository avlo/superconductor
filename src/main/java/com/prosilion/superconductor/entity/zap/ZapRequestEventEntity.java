package com.prosilion.superconductor.entity.zap;

import com.prosilion.superconductor.dto.classified.RelaysTagDto;
import com.prosilion.superconductor.dto.zap.ZapRequestDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "zap")
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
