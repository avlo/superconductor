package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.model.dto.Nip001Dto;
import org.springframework.stereotype.Service;

@Service
public class Nip001Service implements MessageService{
  @Override
  public String processMessage(Nip001Dto dto) {
    return dto.nip001Field();
  }
}
