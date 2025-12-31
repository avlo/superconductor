package com.prosilion.superconductor.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.superconductor.base.service.event.type.KindTypeIF;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AfterimageKindType implements KindTypeIF {
  BADGE_AWARD_REPUTATION(Kind.BADGE_AWARD_EVENT, Kind.BADGE_DEFINITION_EVENT, "BADGE_AWARD_REPUTATION"),
  BADGE_DEFINITION_REPUTATION(Kind.BADGE_DEFINITION_EVENT, Kind.BADGE_DEFINITION_EVENT, "BADGE_DEFINITION_REPUTATION");

  public static final String PLATFORM = "afterimage";
  public static final ExternalIdentityTag BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG = new ExternalIdentityTag(
      PLATFORM,
      "badge_definition_reputation",
      String.valueOf(BadgeDefinitionReputationEvent.class.hashCode()));

  public static final ExternalIdentityTag BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG = new ExternalIdentityTag(
      PLATFORM,
      "badge_award_reputation",
      String.valueOf(BadgeAwardReputationEvent.class.hashCode()));

  private final Kind kind;
  private final Kind kindDefinition;

  @JsonValue
  private final String name;


  @Override
  public KindTypeIF[] getValues() {
    return values();
  }

  @Generated
  public Kind getKind() {
    return this.kind;
  }

  @Generated
  public Kind getKindDefinition() {
    return this.kindDefinition;
  }

  @Generated
  public String getName() {
    return this.name;
  }
}
