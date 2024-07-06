package com.prosilion.superconductor.entity.join.subscriber;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Component
public class SubscriberFilterAuthor extends AbstractFilterType {
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

  @Override
  public String getCode() {
    return "author";
  }
}