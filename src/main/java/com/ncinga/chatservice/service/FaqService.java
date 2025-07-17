package com.ncinga.chatservice.service;

import com.ncinga.chatservice.dto.FaqRequest;
import com.ncinga.chatservice.dto.FaqResponse;

public interface FaqService {
    FaqResponse queryFaq(FaqRequest request);
}
