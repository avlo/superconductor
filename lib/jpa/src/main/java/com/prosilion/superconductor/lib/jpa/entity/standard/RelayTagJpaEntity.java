package com.prosilion.superconductor.lib.jpa.entity.standard;

import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "relay_tag")
public class RelayTagJpaEntity extends AbstractTagJpaEntity {
  private String uri;

  public RelayTagJpaEntity(@NonNull RelayTag relayTag) {
    super("relay");
    this.uri = relayTag.getRelay().getUrl();
  }

  @Override
  @Transient
  public BaseTag getAsBaseTag() {
    return new RelayTag(new Relay(uri));
  }

  @Override
  @Transient
  public List<String> get() {
    return List.of(uri);
  }
}
