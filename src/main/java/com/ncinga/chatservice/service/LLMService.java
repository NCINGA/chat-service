package com.ncinga.chatservice.service;

import com.ncinga.chatservice.dto.LLMRequest;
import com.ncinga.chatservice.dto.LLMResponse;

public interface LLMService {
    public LLMResponse detectIntent(LLMRequest request);

}
