package com.prosilion.superconductor.plugin.tag;

import com.prosilion.superconductor.dto.classified.PriceTagDto;
import com.prosilion.superconductor.entity.classified.PriceTagEntity;
import com.prosilion.superconductor.entity.join.classified.EventEntityPriceTagEntity;
import com.prosilion.superconductor.repository.classified.PriceTagEntityRepository;
import com.prosilion.superconductor.repository.join.classified.EventEntityPriceTagEntityRepository;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import nostr.event.tag.PriceTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PriceTagPlugin<
    P extends PriceTag,
    Q extends PriceTagEntityRepository<R>,
    R extends PriceTagEntity,
    S extends EventEntityPriceTagEntity,
    T extends EventEntityPriceTagEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public PriceTagPlugin(@Nonnull PriceTagEntityRepository<R> repo, @NonNull EventEntityPriceTagEntityRepository<S> join) {
    super(repo, join, "price");
  }

  @Override
  public PriceTagDto getTagDto(@NonNull P priceTag) {
    return new PriceTagDto(priceTag);
  }

  @Override
  public S getEventEntityTagEntity(@NonNull Long eventId, @NonNull Long pricetagId) {
    return (S) new EventEntityPriceTagEntity(eventId, pricetagId);
  }
}
