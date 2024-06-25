package com.prosilion.superconductor.repository.join.subscriber;

import com.prosilion.superconductor.entity.AbstractTagEntity;
import com.prosilion.superconductor.entity.EventEntity;
import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilterEvent;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriberFilterEventRepository<T extends SubscriberFilterEvent> extends AbstractSubscriberFilterTypeJoinRepository<T> {
  @Query("SELECT new com.prosilion.superconductor.entity.EventEntity(e.id, e.eventIdString, e.kind, e.nip, e.pubKey, e.createdAt, e.signature, e.content) from EventEntity e where e.eventIdString = :eventIdString")
  List<EventEntity> findEventsBySubscriberFilterEventString(String eventIdString);

  @Query("SELECT new com.prosilion.superconductor.entity.standard.EventTagEntity(b.eventIdString, b.marker, b.recommendedRelayUrl) from EventTagEntity b where b.id = :eventEntityId")
  List<AbstractTagEntity> findTagsByEventEntityId(Long eventEntityId);

  List<SubscriberFilterEvent> findSubscriberFilterEventsByFilterId(Long filterId);

  void deleteByFilterId(Long filterId);
  void deleteAllByFilterIdIn(List<Long> filterId);

  default void save(@NotNull Long filterId, @NotNull String eventIdString) {
    save((T) new SubscriberFilterEvent(filterId, eventIdString));
  }
}