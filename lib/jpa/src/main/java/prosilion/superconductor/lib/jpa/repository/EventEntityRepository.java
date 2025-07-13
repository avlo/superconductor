package prosilion.superconductor.lib.jpa.repository;

import prosilion.superconductor.lib.jpa.entity.EventEntityIF;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import prosilion.superconductor.lib.jpa.entity.EventEntity;

@Repository
public interface EventEntityRepository extends JpaRepository<EventEntity, Long> {

  //  @Cacheable("events")
//  @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)


  @NonNull
  List<EventEntity> findAll();
//  @NonNull List<EventEntityIF> findAll(Class<EventEntityIF> clazz);

  //  @Cacheable("events")
//  @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)

  @NonNull
  Optional<EventEntity> findById(@NonNull Long id);
//  Optional<EventEntityIF> findById(Class<EventEntityIF> clazz, Long id);

  List<EventEntityIF> findByContent(@NonNull String content);

  Optional<EventEntityIF> findByEventIdString(@NonNull String eventIdString);

  List<EventEntityIF> findByPubKey(@NonNull String pubKey);

  List<EventEntityIF> findByKind(Integer kind);
}
