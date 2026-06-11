package com.denandan.membershipfc.web.controller.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MembershipPlanResponse {
    private Long subscriptionId;
    private String planName;
    private BigDecimal price;
    private String tierName;
}