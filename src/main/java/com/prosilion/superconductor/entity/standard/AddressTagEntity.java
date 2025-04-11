package com.prosilion.superconductor.entity.standard;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.dto.standard.AddressTagDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import nostr.base.PublicKey;
import nostr.base.Relay;
import nostr.event.BaseTag;
import nostr.event.tag.AddressTag;
import nostr.event.tag.IdentifierTag;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "address_tag")
public class AddressTagEntity extends AbstractTagEntity {
  private Integer kind;
  private String pubKey;
  private String uuid;
  private String relayUri;

  public AddressTagEntity(@NonNull AddressTag addressTag) {
    super("a");
    this.kind = addressTag.getKind();
    this.pubKey = addressTag.getPublicKey().toHexString();
    this.uuid = addressTag.getIdentifierTag().getUuid();
    Optional.ofNullable(addressTag.getRelay()).ifPresent(relay -> this.relayUri = relay.getUri());
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
            uuid,
            Optional.ofNullable(relayUri).toString())
        .toList();
  }

  private AddressTag createAddressTag() {
    AddressTag addressTag = new AddressTag();
    addressTag.setKind(kind);
    addressTag.setPublicKey(new PublicKey(pubKey));
    addressTag.setIdentifierTag(new IdentifierTag(uuid));
    Optional.ofNullable(relayUri).ifPresent(relayUri ->
        addressTag.setRelay(new Relay(relayUri)));
    return addressTag;
  }
}
