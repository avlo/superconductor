package com.prosilion.superconductor.h2db.config;

import org.springframework.boot.CommandLineRunner;

public interface DataLoaderJpaIF extends CommandLineRunner {
  String UNIT_UPVOTE = "UNIT_UPVOTE";
  String UNIT_DOWNVOTE = "UNIT_DOWNVOTE";
}
