package com.denandan.membershipfc.strategy;

import com.denandan.membershipfc.web.controller.dto.request.UserMetricsSummary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class GoldTierStrategy implements TierEvaluationStrategy{
    @Override
    public boolean isEligible(UserMetricsSummary userMetricsSummary) {
        return userMetricsSummary.getTotalOrders() >= 50 || "CORPORATE".equalsIgnoreCase(userMetricsSummary.getCohortName());
    }

    @Override
    public String grantedTier() {
        return "GOLD";
    }

    @Override
    public int getEvaluationOrder() {
        return 50;
    }
}
