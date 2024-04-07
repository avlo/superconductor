package com.prosilion.nostrrelay.entity.join;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "subscriber-filter_author-join")
public class SubscriberFilterAuthor implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long subscriberId;
  private String author;

  public SubscriberFilterAuthor(Long subscriberId, String author) {
    this.subscriberId = subscriberId;
    this.author = author;
  }
}