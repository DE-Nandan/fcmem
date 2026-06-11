package com.denandan.membershipfc.web.controller.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ActiveMembershipResponse {
    private String planName;
    private String tierName;
    private LocalDateTime validUntil;
    private String status;
}
