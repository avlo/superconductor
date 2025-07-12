package prosilion.superconductor.lib.jpa.repository.standard;

import prosilion.superconductor.lib.jpa.entity.standard.AddressTagEntity;
import prosilion.superconductor.lib.jpa.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressTagEntityRepository<T extends AddressTagEntity> extends AbstractTagEntityRepository<T> {
}
