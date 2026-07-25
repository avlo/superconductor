package com.prosilion.superconductor.base;

import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import lombok.NonNull;

public abstract class BaseIntegrationTestFixtures {
  public static final String TEST_UNIT_REPUTATION = "BADGE_DEFN_UNIT_REP";
  public static final String AWARD_UNIT_UPVOTE = "BDG_DEF_UNIT_UP";
  public static final String AWARD_UNIT_DOWNVOTE = "BDG_DEF_UNIT_DOWN";
  public static final String FORMULA_UNIT_UPVOTE = "FORMULA_UNIT_UPVOTE";
  public static final String FORMULA_UNIT_DOWNVOTE = "FORMULA_UNIT_DOWNVOTE";
  public static final String PLUS_ONE_FORMULA = "+1";
  public static final String MINUS_ONE_FORMULA = "-1";
  protected final IdentifierTag reputationIdentifierTag = new IdentifierTag(TEST_UNIT_REPUTATION);
  protected final IdentifierTag upvoteIdentifierTag = new IdentifierTag(AWARD_UNIT_UPVOTE);
  protected final IdentifierTag downvoteIdentifierTag = new IdentifierTag(AWARD_UNIT_DOWNVOTE);
  protected final IdentifierTag formulaUpvoteIdentifierTag = new IdentifierTag(FORMULA_UNIT_UPVOTE);
  protected final IdentifierTag formulaDownvoteIdentifierTag = new IdentifierTag(FORMULA_UNIT_DOWNVOTE);
  protected final Identity aImgIdentity;
  protected final Identity submitter =
//     Identity.generateRandomIdentity();
     Identity.create("aaa4585483196998204846989544737603523651520600328805626488477202");
  protected final Identity upvoteDefnCreator =
//     Identity.generateRandomIdentity();
     Identity.create("bbb4585483196998204846989544737603523651520600328805626488477202");
  protected final Identity recipient =
//     Identity.generateRandomIdentity();
     Identity.create("ccc4585483196998204846989544737603523651520600328805626488477202");
  protected final Identity formulaCreator =
//     Identity.generateRandomIdentity();
     Identity.create("ddd4585483196998204846989544737603523651520600328805626488477202");
  protected final Identity repDefnCreator =
//     Identity.generateRandomIdentity();
     Identity.create("eee4585483196998204846989544737603523651520600328805626488477202");

  public BaseIntegrationTestFixtures(@NonNull Identity superconductorInstanceIdentity) {
    this.aImgIdentity = superconductorInstanceIdentity;
  }
}
