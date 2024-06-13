package com.prosilion.superconductor.entity.zap;

import com.prosilion.superconductor.dto.standard.RelaysTagDto;
import com.prosilion.superconductor.dto.zap.ZapRequestDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
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
