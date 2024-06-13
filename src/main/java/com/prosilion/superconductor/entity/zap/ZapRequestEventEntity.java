package com.prosilion.superconductor.entity.zap;

import com.prosilion.superconductor.dto.standard.RelaysTagDto;
import com.prosilion.superconductor.dto.zap.ZapRequestDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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

  public ZapRequestEventEntity(@NonNull String recipientPubKey, @NonNull Long amount, @NonNull String lnUrl) {
    this.recipientPubKey = recipientPubKey;
    this.amount = amount;
    this.lnUrl = lnUrl;
  }

  public ZapRequestDto convertEntityToDto(@NonNull RelaysTagDto relaysTagDto) {
    return new ZapRequestDto(recipientPubKey, amount, lnUrl, relaysTagDto);
  }
}
