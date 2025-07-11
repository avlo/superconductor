package com.prosilion.superconductor.entity.standard;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.dto.standard.AddressTagDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;



import java.net.URI;
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
@RedisHash("address_tag")
public class AddressTagEntity extends AbstractTagEntity {
  private Integer kind;
  private String pubKey;
  private String uuid;
  private String relayUri;

  public AddressTagEntity(@NonNull AddressTag addressTag) {
    super("a");
    this.kind = addressTag.getKind().getValue();
    this.pubKey = addressTag.getPublicKey().toHexString();
    Optional.ofNullable(addressTag.getIdentifierTag()).ifPresent(uuid -> this.uuid = uuid.getUuid());
    Optional.ofNullable(addressTag.getRelay()).ifPresent(relay -> this.relayUri = relay.getUri().toString());
  }

  @Override
  @org.springframework.data.annotation.Transient
  public BaseTag getAsBaseTag() {
    return createAddressTag();
  }

  @Override
  public AbstractTagDto convertEntityToDto() {
    return new AddressTagDto(createAddressTag());
  }

  @Override
  @org.springframework.data.annotation.Transient
  public List<String> get() {
    List<String> list = Stream.of(
            kind.toString(),
            pubKey,
            Optional.ofNullable(uuid).toString(),
            relayUri)
        .toList();
    return list;
  }

//  TODO: stream-ify below
  private AddressTag createAddressTag() {
    Optional<IdentifierTag> identifierTag = Optional.ofNullable(uuid).map(IdentifierTag::new);
    Optional<Relay> relayTag = Optional.ofNullable(relayUri).map(s -> new Relay(URI.create(s)));

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
