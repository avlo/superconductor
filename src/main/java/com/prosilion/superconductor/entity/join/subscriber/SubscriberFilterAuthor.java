package com.prosilion.superconductor.entity.join.subscriber;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SubscriberFilterAuthor that = (SubscriberFilterAuthor) o;
    return Objects.equals(author, that.author);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(author);
  }
}