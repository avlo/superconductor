package com.prosilion.superconductor.entity.standard;

import com.prosilion.superconductor.dto.standard.PubkeyTagDto;
import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import nostr.base.PublicKey;
import nostr.event.BaseTag;
import nostr.event.tag.PubKeyTag;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "pubkey_tag")
public class PubkeyTagEntity extends AbstractTagEntity {
  //  TODO: below annotations and id necessary for compilation even thuogh same is defined in StandardTagEntity
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String publicKey;
  private String mainRelayUrl;
  private String petName;

  public PubkeyTagEntity(@NonNull PubKeyTag pubKeyTag) {
    this.publicKey = pubKeyTag.getPublicKey().toString();
    this.mainRelayUrl = pubKeyTag.getMainRelayUrl();
    this.petName = pubKeyTag.getPetName();
  }

  @Override
  public String getCode() {
    return "p";
  }

  @Override
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PubkeyTagEntity that = (PubkeyTagEntity) o;
    return Objects.equals(publicKey, that.publicKey) && Objects.equals(mainRelayUrl, that.mainRelayUrl) && Objects.equals(petName, that.petName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(publicKey, mainRelayUrl, petName);
  }
}
