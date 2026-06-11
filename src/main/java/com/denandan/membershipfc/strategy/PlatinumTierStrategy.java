package com.denandan.membershipfc.strategy;

import com.denandan.membershipfc.web.controller.dto.request.UserMetricsSummary;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Getter
@Setter
public class PlatinumTierStrategy implements TierEvaluationStrategy{
    @Override
    public boolean isEligible(UserMetricsSummary userMetricsSummary) {
        return userMetricsSummary.getMonthlySpend().compareTo(new BigDecimal("1000.00")) >= 0;
    }

    @Override
    public String grantedTier() {
        return "PLATINUM";
    }

    @Override
    public int getEvaluationOrder() {
        return 100;
    }
}
