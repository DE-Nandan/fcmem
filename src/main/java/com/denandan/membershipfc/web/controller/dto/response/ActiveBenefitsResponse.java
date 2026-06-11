package com.denandan.membershipfc.web.controller.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class ActiveBenefitsResponse {
    private Long userId;
    private String tierName;
    private Map<String, String> availablePerks;
}