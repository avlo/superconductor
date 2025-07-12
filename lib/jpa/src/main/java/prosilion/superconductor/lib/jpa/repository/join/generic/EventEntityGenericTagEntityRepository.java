package prosilion.superconductor.lib.jpa.repository.join.generic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import prosilion.superconductor.lib.jpa.entity.join.generic.EventEntityGenericTagEntity;

@Repository
public interface EventEntityGenericTagEntityRepository extends JpaRepository<EventEntityGenericTagEntity, Long> {
  List<EventEntityGenericTagEntity> findByEventId(Long eventId);
}
