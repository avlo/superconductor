package com.prosilion.superconductor.entity.standard;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.dto.standard.PubkeyTagDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;



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
@RedisHash("pubkey_tag")
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
  @org.springframework.data.annotation.Transient
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
  @org.springframework.data.annotation.Transient
  public List<String> get() {
    return Stream.of(
            this.publicKey,
            Optional.ofNullable(this.mainRelayUrl).toString(),
            Optional.ofNullable(this.petName).toString())
        .toList();
  }
}
