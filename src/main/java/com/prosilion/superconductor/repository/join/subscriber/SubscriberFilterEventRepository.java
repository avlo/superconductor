package com.prosilion.superconductor.repository.join.subscriber;

import com.prosilion.superconductor.entity.BaseTagEntity;
import com.prosilion.superconductor.entity.EventEntity;
import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilterEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriberFilterEventRepository extends JpaRepository<SubscriberFilterEvent, Long> {
  @Query("SELECT new com.prosilion.superconductor.entity.EventEntity(e.id, e.eventId, e.kind, e.nip, e.pubKey, e.createdAt, e.signature, e.content) from EventEntity e where e.eventId = :eventId")
  Optional<List<EventEntity>> findEventsBySubscriberFilterEventString(String eventId);

  @Query("SELECT new com.prosilion.superconductor.entity.BaseTagEntity(b.key, b.idEvent, b.marker) from BaseTagEntity b where b.id = :eventEntityId")
  Optional<List<BaseTagEntity>> findBaseTagsByEventEntityId(Long eventEntityId);

  Optional<List<SubscriberFilterEvent>> findSubscriberFilterEventsByFilterId(Long filterId);

  void deleteByFilterId(Long filterId);
  void deleteAllByFilterIdIn(List<Long> filterId);

  default void save(Long filterId, String eventId) {
    this.save(new SubscriberFilterEvent(filterId, eventId));
  }
}