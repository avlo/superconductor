package com.prosilion.superconductor.base;

import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import lombok.NonNull;

public abstract class BaseIntegrationTestFixtures {
  public static final Relay relay = new Relay("ws://localhost:5555");

  public static final String TEST_UNIT_REPUTATION = "BADGE_DEFN_UNIT_REP";
  public static final String AWARD_UNIT_UPVOTE = "BDG_DEF_UNIT_UP";
  public static final String AWARD_UNIT_DOWNVOTE = "BDG_DEF_UNIT_DOWN";
  public static final String FORMULA_UNIT_UPVOTE = "FORMULA_UNIT_UPVOTE";
  public static final String FORMULA_UNIT_DOWNVOTE = "FORMULA_UNIT_DOWNVOTE";
  public static final String PLUS_ONE_FORMULA = "+1";
  public static final String MINUS_ONE_FORMULA = "-1";
  public static final IdentifierTag reputationIdentifierTag = new IdentifierTag(TEST_UNIT_REPUTATION);
  public static final IdentifierTag upvoteIdentifierTag = new IdentifierTag(AWARD_UNIT_UPVOTE);
  public static final IdentifierTag downvoteIdentifierTag = new IdentifierTag(AWARD_UNIT_DOWNVOTE);
  public static final IdentifierTag formulaUpvoteIdentifierTag = new IdentifierTag(FORMULA_UNIT_UPVOTE);
  public static final IdentifierTag formulaDownvoteIdentifierTag = new IdentifierTag(FORMULA_UNIT_DOWNVOTE);
  public static final Identity submitter =
//     Identity.generateRandomIdentity();
     Identity.create("aaa4585483196998204846989544737603523651520600328805626488477202");
  public static final Identity upvoteDefnCreator =
//     Identity.generateRandomIdentity();
     Identity.create("bbb4585483196998204846989544737603523651520600328805626488477202");
  public static final Identity recipient =
//     Identity.generateRandomIdentity();
     Identity.create("ccc4585483196998204846989544737603523651520600328805626488477202");
  public static final Identity formulaCreator =
//     Identity.generateRandomIdentity();
     Identity.create("ddd4585483196998204846989544737603523651520600328805626488477202");
  public static final Identity repDefnCreator =
//     Identity.generateRandomIdentity();
     Identity.create("eee4585483196998204846989544737603523651520600328805626488477202");
  public static final Identity aImgIdentity =
     Identity.create("fa11661b5f43c8f18f11861b4d553c47337dac9e351083b27320e311b7b324ac");
  
  public final Identity parameterAimgIdentity;

  public BaseIntegrationTestFixtures(@NonNull Identity superconductorInstanceIdentity) {
    this.parameterAimgIdentity = superconductorInstanceIdentity;
  }
}
