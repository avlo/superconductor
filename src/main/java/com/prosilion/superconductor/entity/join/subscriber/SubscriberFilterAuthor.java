package com.prosilion.superconductor.entity.join.subscriber;

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

  private Long filterId;
  private String author;

  public SubscriberFilterAuthor(Long filterId, String author) {
    this.filterId = filterId;
    this.author = author;
  }
}