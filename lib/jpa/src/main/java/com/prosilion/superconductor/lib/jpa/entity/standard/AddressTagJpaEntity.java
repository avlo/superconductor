package com.prosilion.superconductor.lib.jpa.entity.standard;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.lib.jpa.dto.AbstractTagDto;
import com.prosilion.superconductor.lib.jpa.dto.standard.AddressTagDto;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "address_tag")
public class AddressTagJpaEntity extends AbstractTagJpaEntity {
  private Integer kind;
  private String pubKey;
  private String uuid;
  private String relayUri;

  public AddressTagJpaEntity(@NonNull AddressTag addressTag) {
    super("a");
    this.kind = addressTag.getKind().getValue();
    this.pubKey = addressTag.getPublicKey().toHexString();
    Optional.ofNullable(addressTag.getIdentifierTag()).ifPresent(uuid -> this.uuid = uuid.getUuid());
    Optional.ofNullable(addressTag.getRelay()).ifPresent(relay -> this.relayUri = relay.getUrl());
  }

  @Override
  @Transient
  public BaseTag getAsBaseTag() {
    return createAddressTag();
  }

  @Override
  public AbstractTagDto convertEntityToDto() {
    return new AddressTagDto(createAddressTag());
  }

  @Override
  @Transient
  public List<String> get() {
    return Stream.of(
            kind.toString(),
            pubKey,
            Optional.ofNullable(uuid).toString(),
            relayUri)
        .toList();
  }

  //  TODO: stream-ify below
  private AddressTag createAddressTag() {
    Optional<IdentifierTag> identifierTag = Optional.ofNullable(uuid).map(IdentifierTag::new);
    Optional<Relay> relayTag = Optional.ofNullable(relayUri).map(Relay::new);

    if (identifierTag.isPresent() && relayTag.isPresent()) {
      return new AddressTag(
          Kind.valueOf(kind),
          new PublicKey(pubKey),
          identifierTag.orElseThrow(),
          relayTag.get()
      );
    }

    if (identifierTag.isPresent()) {
      return new AddressTag(
          Kind.valueOf(kind),
          new PublicKey(pubKey),
          identifierTag.orElseThrow()
      );
    }

    return new AddressTag(
        Kind.valueOf(kind),
        new PublicKey(pubKey)
    );
  }
}
