package com.prosilion.nostrrelay.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Message {
  private String from;
  private String to;
  private String content;
}