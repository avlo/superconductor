package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.dto.classified.PriceTagDto;
import com.prosilion.superconductor.entity.classified.PriceTagEntity;
import com.prosilion.superconductor.entity.join.classified.EventEntityPriceTagEntity;
import com.prosilion.superconductor.repository.classified.PriceTagEntityRepository;
import com.prosilion.superconductor.repository.join.classified.EventEntityPriceTagEntityRepository;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.tag.PriceTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class PriceTagModule<
    P extends PriceTag,
    Q extends PriceTagEntityRepository<R>,
    R extends PriceTagEntity,
    S extends EventEntityPriceTagEntity,
    U extends EventEntityPriceTagEntityRepository<S>>
    implements TagModule<P, Q, R, S, U> {

  private final PriceTagEntityRepository<R> priceTagEntityRepository;
  private final EventEntityPriceTagEntityRepository<S> join;

  @Autowired
  public PriceTagModule(@Nonnull PriceTagEntityRepository<R> priceTagEntityRepository, @NonNull EventEntityPriceTagEntityRepository<S> join) {
    this.priceTagEntityRepository = priceTagEntityRepository;
    this.join = join;
  }

  @Override
  public String getCode() {
    return "price";
  }

  @Override
  public Class<R> getClazz() {
    return (Class<R>) PriceTagEntity.class;
  }

  @Override
  public R convertDtoToEntity(P priceTag) {
    return (R) getTagDto(priceTag).convertDtoToEntity();
  }

  @Override
  public PriceTagDto getTagDto(P priceTag) {
    return new PriceTagDto(priceTag);
  }

  @Override
  public S getEventEntityTagEntity(Long eventId, Long pricetagId) {
    return (S) new EventEntityPriceTagEntity(eventId, pricetagId);
  }

  @Override
  public U getEventEntityStandardTagEntityRepositoryJoin() {
    return (U) join;
  }

  @Override
  public Q getStandardTagEntityRepositoryRxR() {
    return (Q) priceTagEntityRepository;
  }
}
