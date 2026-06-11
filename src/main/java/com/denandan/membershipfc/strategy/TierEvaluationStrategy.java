package com.denandan.membershipfc.strategy;

import com.denandan.membershipfc.web.controller.dto.request.UserMetricsSummary;
import lombok.Getter;

public interface TierEvaluationStrategy {
    boolean isEligible(UserMetricsSummary userMetricsSummary);

    String grantedTier();

    int getEvaluationOrder();
}
