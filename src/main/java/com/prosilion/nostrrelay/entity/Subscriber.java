package com.prosilion.nostrrelay.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@Entity
public class Subscriber implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String subscriberId;
  private String session;

  public Subscriber(@NonNull String subscriberId, @NonNull String session) {
    this.subscriberId = subscriberId;
    this.session = session;
  }
}
