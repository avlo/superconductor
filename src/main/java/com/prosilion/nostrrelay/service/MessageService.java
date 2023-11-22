package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.model.dto.Nip001Dto;

public interface MessageService {
  String processMessage(Nip001Dto dto);
}
