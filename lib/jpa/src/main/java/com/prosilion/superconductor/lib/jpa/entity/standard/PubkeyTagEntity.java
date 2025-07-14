package com.prosilion.superconductor.lib.jpa.entity.standard;

import com.prosilion.superconductor.lib.jpa.dto.AbstractTagDto;
import com.prosilion.superconductor.lib.jpa.dto.standard.PubkeyTagDto;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import lombok.Setter;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.PubKeyTag;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "pubkey_tag")
// TODO: comprehensive unit test all parameter variants
public class PubkeyTagEntity extends AbstractTagEntity {
  private String publicKey;
  private String mainRelayUrl;
  private String petName;

  public PubkeyTagEntity(@NonNull PubKeyTag pubKeyTag) {
    super("p");
    this.publicKey = pubKeyTag.getPublicKey().toHexString();
    this.mainRelayUrl = pubKeyTag.getMainRelayUrl();
    this.petName = pubKeyTag.getPetName();
  }

  @Override
  @Transient
  public BaseTag getAsBaseTag() {
    return new PubKeyTag(new PublicKey(publicKey), mainRelayUrl, petName);
  }

  @Override
  public AbstractTagDto convertEntityToDto() {
    return new PubkeyTagDto(
        new PubKeyTag(
            new PublicKey(publicKey), mainRelayUrl, petName));
  }

  @Override
  @Transient
  public List<String> get() {
    return Stream.of(
            this.publicKey,
            Optional.ofNullable(this.mainRelayUrl).toString(),
            Optional.ofNullable(this.petName).toString())
        .toList();
  }
}
