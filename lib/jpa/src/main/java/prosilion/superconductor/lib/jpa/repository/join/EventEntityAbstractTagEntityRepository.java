package prosilion.superconductor.lib.jpa.repository.join;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractEntity;

@NoRepositoryBean
public interface EventEntityAbstractTagEntityRepository<T extends EventEntityAbstractEntity> extends JpaRepository<T, Long> {
  List<T> findByEventId(Long eventId);
  String getCode();
}
