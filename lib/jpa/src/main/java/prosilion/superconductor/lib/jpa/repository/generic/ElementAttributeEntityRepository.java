package prosilion.superconductor.lib.jpa.repository.generic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import prosilion.superconductor.lib.jpa.entity.generic.ElementAttributeEntity;

@Repository
public interface ElementAttributeEntityRepository extends JpaRepository<ElementAttributeEntity, Long> {
}
