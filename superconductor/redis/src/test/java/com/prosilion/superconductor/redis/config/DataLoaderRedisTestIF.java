package com.prosilion.superconductor.redis.config;

import org.springframework.boot.CommandLineRunner;

public interface DataLoaderRedisTestIF extends CommandLineRunner {
  String TEST_UNIT_UPVOTE = "BADGE_DEFINITION_UNIT_UPVOTE";
  String TEST_UNIT_DOWNVOTE = "BADGE_DEFINITION_UNIT_DOWNVOTE";
  String TEST_UNIT_REPUTATION = "BADGE_DEFINITION_UNIT_REPUTATION";
}
