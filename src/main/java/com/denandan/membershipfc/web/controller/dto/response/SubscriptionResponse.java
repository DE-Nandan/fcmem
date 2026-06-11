package com.denandan.membershipfc.web.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    private Long userSubscriptionId;
    private Long catalogPlanId;
    private String planName;
    private String tierName;
    private LocalDateTime validUntil;
    private String status;
}