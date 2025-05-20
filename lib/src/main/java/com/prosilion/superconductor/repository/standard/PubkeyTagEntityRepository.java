package com.prosilion.superconductor.repository.standard;

import com.prosilion.superconductor.entity.standard.PubkeyTagEntity;
import com.prosilion.superconductor.repository.AbstractTagEntityRepository;
import java.util.List;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface PubkeyTagEntityRepository<T extends PubkeyTagEntity> extends AbstractTagEntityRepository<T> {
  List<T> findByPublicKey(@NonNull String publicKey);
}
