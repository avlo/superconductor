package com.prosilion.superconductor.redis.config;

import com.prosilion.nostr.user.Identity;
import org.springframework.boot.CommandLineRunner;

public interface DataLoaderRedisTestIF extends CommandLineRunner {
  public final static Identity aImgIdentity =
//     Identity.generateRandomIdentity();
     Identity.create("2684585483196998204846989544737603523651520600328805626488477202");

  public final static Identity submitter =
//     Identity.generateRandomIdentity();
     Identity.create("aaa4585483196998204846989544737603523651520600328805626488477202");

  public final static Identity upvoteDefnCreator =
//     Identity.generateRandomIdentity();
     Identity.create("bbb4585483196998204846989544737603523651520600328805626488477202");

  public final static Identity recipient =
//     Identity.generateRandomIdentity();
     Identity.create("ccc4585483196998204846989544737603523651520600328805626488477202");

  public final static Identity formulaCreator =
//     Identity.generateRandomIdentity();
     Identity.create("ddd4585483196998204846989544737603523651520600328805626488477202");

  public final static Identity repDefnCreator =
//     Identity.generateRandomIdentity();
     Identity.create("eee4585483196998204846989544737603523651520600328805626488477202");
  
  String TEST_UNIT_UPVOTE = "BDG_DEF_UNIT_UP";
  String TEST_UNIT_DOWNVOTE = "BDG_DEF_UNIT_DOWN";
  String TEST_UNIT_REPUTATION = "BADGE_DEFN_UNIT_REP";
}
