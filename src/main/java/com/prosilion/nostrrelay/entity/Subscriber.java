package com.prosilion.nostrrelay.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;

@Setter
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
