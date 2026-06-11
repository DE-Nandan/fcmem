package com.denandan.membershipfc.web.controller.dto.request;


import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class UserMetricsSummary {
    private Long userId;
    private int totalOrders;
    private BigDecimal monthlySpend;
    private String cohortName;
}