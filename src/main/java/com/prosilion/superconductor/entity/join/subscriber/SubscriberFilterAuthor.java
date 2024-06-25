package com.prosilion.superconductor.entity.join.subscriber;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "subscriber-filter_author")
public class SubscriberFilterAuthor extends AbstractSubscriberFilter {
  private String author;

  public SubscriberFilterAuthor(Long filterId, String author) {
    super(filterId);
    this.author = author;
  }
}