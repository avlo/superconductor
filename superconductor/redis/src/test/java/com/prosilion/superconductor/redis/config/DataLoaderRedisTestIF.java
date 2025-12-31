package com.prosilion.superconductor.redis.config;

import org.springframework.boot.CommandLineRunner;

public interface DataLoaderRedisTestIF extends CommandLineRunner {
  String TEST_UNIT_UPVOTE = "TEST_UNIT_UPVOTE";
  String TEST_UNIT_DOWNVOTE = "TEST_UNIT_DOWNVOTE";
  String TEST_UNIT_REPUTATION = "TEST_UNIT_REPUTATION";
}
