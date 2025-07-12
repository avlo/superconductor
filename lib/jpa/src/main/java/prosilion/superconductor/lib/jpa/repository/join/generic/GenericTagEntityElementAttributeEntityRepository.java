package prosilion.superconductor.lib.jpa.repository.join.generic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import prosilion.superconductor.lib.jpa.entity.join.generic.GenericTagEntityElementAttributeEntity;

@Repository
public interface GenericTagEntityElementAttributeEntityRepository extends JpaRepository<GenericTagEntityElementAttributeEntity, Long> {
  List<GenericTagEntityElementAttributeEntity> getAllByGenericTagId(Long id);
}
