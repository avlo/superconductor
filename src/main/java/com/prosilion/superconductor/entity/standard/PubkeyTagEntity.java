package com.prosilion.superconductor.entity.standard;

import com.prosilion.superconductor.dto.PubkeyTagDto;
import com.prosilion.superconductor.dto.StandardTagDto;
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

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "pubkey_tag")
public class PubkeyTagEntity extends StandardTagEntity {
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
  public Character getCode() {
    return 'p';
  }

  @Override
  public BaseTag getAsBaseTag() {
    return new PubKeyTag(new PublicKey(publicKey), mainRelayUrl, petName);
  }

  @Override
  public StandardTagDto convertEntityToDto() {
    return new PubkeyTagDto(new PubKeyTag(new PublicKey(publicKey), mainRelayUrl, petName));
  }
}
