package com.prosilion.superconductor.lib.jpa.plugin.tag;

import jakarta.annotation.Nonnull;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.PriceTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.prosilion.superconductor.lib.jpa.dto.classified.PriceTagDto;
import com.prosilion.superconductor.lib.jpa.entity.classified.PriceTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.join.classified.EventEntityPriceTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.classified.PriceTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.join.classified.EventEntityPriceTagJpaEntityRepository;

@Component
public class PriceTagPlugin<
    P extends PriceTag,
    Q extends PriceTagJpaEntityRepository<R>,
    R extends PriceTagJpaEntity,
    S extends EventEntityPriceTagJpaEntity,
    T extends EventEntityPriceTagJpaEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public PriceTagPlugin(@Nonnull PriceTagJpaEntityRepository<R> repo, @NonNull EventEntityPriceTagJpaEntityRepository<S> join) {
    super(repo, join, "price");
  }

  @Override
  public PriceTagDto getTagDto(@NonNull P priceTag) {
    return new PriceTagDto(priceTag);
  }

  @Override
  public S getEventEntityTagJpaEntity(@NonNull Long eventId, @NonNull Long pricetagId) {
    return (S) new EventEntityPriceTagJpaEntity(eventId, pricetagId);
  }
}
